# `kotlinx.serialization` :heart: `DynamoDB`

DynamoDB format for [kotlinx.serialization](https://github.com/kotlin/kotlinx.serialization). Serialize and deserialize documents from DynamoDB using `kotlinx.serialization`. This library allows you to map DynamoDB documents to / from domain object.

## Introduction

According with Wikipedia, [Amazon DynamoDB](https://aws.amazon.com/dynamodb/) is a fully managed proprietary NoSQL database offered by Amazon.com.

When using [DynamoDB - Kotlin SDK](https://central.sonatype.com/artifact/aws.sdk.kotlin/dynamodb), the SDK returns documents using [AttributeValue](https://sdk.amazonaws.com/kotlin/api/latest/dynamodb/aws.sdk.kotlin.services.dynamodb.model/-attribute-value/index.html). This library allows you to easily convert AttributeValue to Kotlin objects using the power of `kotlinx.serialization`.

## Setup

To use the DynaMap library, you have to perform serialization and deserialization similar to how you would with kotlinx.serialization.

1. Install `kotlinx.serialization` plugin.
2. Add `DynaMap` serialization dependency.

> Kotlin DSL:

```kotlin
plugins {
    kotlin("jvm")
    
    // ADD SERIALIZATION PLUGIN
    kotlin("plugin.serialization")
}

dependencies {
    // ADD SERIALIZATION DEPENDENCY
    implementation("com.codanbaru.kotlin:dynamap:0.9.0")
}
```

<details>
    <summary>Groovy DSL</summary>

```gradle
plugins {
    // ADD SERIALIZATION PLUGIN
    id 'org.jetbrains.kotlin.plugin.serialization'
}    

dependencies {
    // ADD SERIALIZATION DEPENDENCY
    implementation 'com.codanbaru.kotlin:dynamap:0.9.0'
}
```
</details>

## Simple Example

```kotlin
@Serializable
data class Book(val name: String, val author: String?)

val dynamap = Dynamap {
    evaluateUndefinedAttributesAsNullAttribute = false
}

fun encodeBook(book: Book): Map<String, AttributeValue> {
    val item: Map<String, AttributeValue> = dynamap.encodeToItem(book)
  
    return item
}

fun decodeBook(item: Map<String, AttributeValue>): Book {{
    val obj: Book = dynamap.decodeFromItem(item)
  
    return obj
}
```

By default, the `DynaMap` library evaluates `undefined` attributes as `null` attributes. This means that if the document in the database does not contain an attribute for a nullable property, `DynaMap` will create that attribute automatically.

```kotlin
@Serializable
data class Book(val name: String, val author: String?, val price: Int)

val itemOnDatabase = mapOf(
    "name" to AttributeValue.S("Harry Potter"),
    "price" to AttributeValue.N("10")
)

// Will deserialize object successfully.
val obj1 = Dynamap { evaluateUndefinedAttributesAsNullAttribute = true }.decodeFromItem<Book>(itemOnDatabase)

// Will raise DynamapSerializationException.UnexpectedUndefined exception.
val obj2 = Dynamap { evaluateUndefinedAttributesAsNullAttribute = false }.decodeFromItem<Book>(itemOnDatabase)
```


## Property Mapping

`DynaMap` can override the name used in encoded document using `@SerialName` annotation.

```kotlin
@Serializable
data class Book(
    @SerialName("BookName")
    val name: String,
    
    val author: String?,
    val price: Int
)

val book = Book("Harry Potter", "JKRowling", 10)

val item = Dynamap.encodeToItem(book)
println(item) // {BookName=S(value=Harry Potter), author=S(value=JKRowling), price=N(value=10)}
```

## Binary Serialization / Deserialization

`kotlinx.serialization` manages `ByteArray` internally as a list of bytes. So, if we try to serialize / deserialize it with default serializer, it will convert the `ByteArray` to a list of numbers in DynamoDB.

```kotlin
@Serializable
data class User(val username: String, val passwordHash: ByteArray)

val user = User(username = "Codanbaru", passwordHash = "AQIDBA==".decodeBase64Bytes())
val item: Map<String, AttributeValue> = Dynamap.encodeToItem(user)

println(item) // {username=S(value=Codanbaru), passwordHash=L(value=[N(value=1), N(value=2), N(value=3), N(value=4)])}
```

To convert `ByteArray` to Binary type on DynamoDB, you can use `DynamoBinarySerializer`.

```kotlin
@Serializable
data class User(
    val username: String,

    @Serializable(with = DynamoBinarySerializer::class)
    val passwordHash: ByteArray
)

val user = User(username = "Codanbaru", passwordHash = "AQIDBA==".decodeBase64Bytes())
val item: Map<String, AttributeValue> = Dynamap.encodeToItem(user)

println(item) // {username=S(value=Codanbaru), passwordHash=B(value=[1, 2, 3, 4])}
```

## Polymorphism

`DynaMap` also supports serialization / deserialization of polyphormic structures<sup>1</sup>.

At the moment, `Dynamap` currently support `Closed polymorphism` only.

- :white_check_mark: Closed polymorphism
- :x: Open polymorphism

<sup>1</sup> You can get more information about polymorphism on `kotlinx.serialization` documentation.

```kotlin
val dynamap = Dynamap { }

@Serializable
sealed class Social {
    @Serializable
    data class Email(
        var email: String
    ) : Social()

    @Serializable
    data class Phone(
        val country: String,
        val number: String
    ) : Social()

    @Serializable
    data class Instagram(
        val username: String
    ) : Social()
}

@Serializable
data class User(val name: String, val social: Social)

val user0 = User(name = "Codanbaru 0", social = Social.Email("demo@codanbaru.com"))
val user1 = User(name = "Codanbaru 0", social = Social.Instagram("@codanbaru"))

val item0: Map<String, AttributeValue> = dynamap.encodeToItem(user0)
val item1: Map<String, AttributeValue> = dynamap.encodeToItem(user1)

println(item0) // {name=S(value=Codanbaru 0), social=M(value={email=S(value=demo@codanbaru.com), __dynamap_serialization_type=S(value=com.codanbaru.app.Social.Email)})}
println(item1) // {name=S(value=Codanbaru 0), social=M(value={username=S(value=@codanbaru), __dynamap_serialization_type=S(value=com.codanbaru.app.Social.Instagram)})}
```

You can specify, at `Dynamap` creation, which key `kotlinx.serialization` should use to discriminate classes.

```kotlin
val dynamap = Dynamap {
    classDiscriminator = "type"
}

// ...

println(item0) // {name=S(value=Codanbaru 0), social=M(value={email=S(value=demo@codanbaru.com), type=S(value=com.codanbaru.app.Social.Email)})}
println(item1) // {name=S(value=Codanbaru 0), social=M(value={username=S(value=@codanbaru), type=S(value=com.codanbaru.app.Social.Instagram)})}
```

Also, with `@SerialName` annotation, you can override the class name used by `kotlinx.serialization` and `Dynamap`.

```kotlin
@Serializable
sealed class Social {
    @Serializable
    @SerialName("email")
    data class Email(
        var email: String
    ) : Social()

    @Serializable
    @SerialName("phone")
    data class Phone(
        val country: String,
        val number: String
    ) : Social()

    @Serializable
    @SerialName("instagram")
    data class Instagram(
        val username: String
    ) : Social()
}

// ...

println(item0) // {name=S(value=Codanbaru 0), social=M(value={email=S(value=demo@codanbaru.com), type=S(value=com.codanbaru.app.Social.Email)})}
println(item1) // {name=S(value=Codanbaru 0), social=M(value={username=S(value=@codanbaru), type=S(value=com.codanbaru.app.Social.Instagram)})}
```
