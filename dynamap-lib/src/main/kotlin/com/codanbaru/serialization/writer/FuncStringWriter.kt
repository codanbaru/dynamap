package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun String.writeString(type: DynamoType, configuration: DynamapConfiguration): AttributeValue {
    return when (type) {
        DynamoType.STRING -> writeStringAsString(configuration)
        DynamoType.BINARY -> writeStringAsBinary(configuration)
        DynamoType.NUMBER -> writeStringAsNumber(configuration)
        else -> throw PrimitiveWriterException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.STRING, DynamoType.BINARY, DynamoType.NUMBER)
        )
    }
}

private fun String.writeStringAsString(configuration: DynamapConfiguration): AttributeValue {
    return AttributeValue.S(this)
}
private fun String.writeStringAsBinary(configuration: DynamapConfiguration): AttributeValue {
    return AttributeValue.B(toByteArray(Charsets.UTF_8))
}
private fun String.writeStringAsNumber(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this working when string is not a number?
    return AttributeValue.N(this)
}
