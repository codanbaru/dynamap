package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.dynamodb.DynamoType
import com.codanbaru.serialization.format.DynamapFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

public abstract class Dynamap internal constructor(
    val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule
): DynamapFormat {

    public companion object Default : Dynamap(DynamapConfiguration(), EmptySerializersModule())
    internal class Impl(configuration: DynamapConfiguration, serializersModule: SerializersModule): Dynamap(configuration, serializersModule)

    override fun <T> encodeToAttribute(serializer: SerializationStrategy<T>, value: T): AttributeValue {
        lateinit var attributeValue: AttributeValue
        val encoder = DynamoEncoder(
            property = "\$",

            desiredType = null,

            configuration = configuration,
            serializersModule = serializersModule,
            consumer = { attributeValue = it  }
        )
        encoder.encodeSerializableValue(serializer, value)
        return attributeValue
    }

    override fun <T> decodeFromAttribute(deserializer: DeserializationStrategy<T>, value: AttributeValue): T {
        val decoder = DynamoDecoder(
            attributeValue = value,

            property = "\$",

            desiredType = null,

            configuration = configuration,
            serializersModule = serializersModule
        )
        return decoder.decodeSerializableValue(deserializer)
    }
}