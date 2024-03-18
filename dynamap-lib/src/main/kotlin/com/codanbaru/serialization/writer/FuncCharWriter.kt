package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun Char.writeChar(type: DynamoType, configuration: DynamapConfiguration): AttributeValue {
    return when (type) {
        DynamoType.NUMBER -> writeCharAsNumber(configuration)
        DynamoType.STRING -> writeCharAsString(configuration)
        // DynamoType.BINARY -> writeCharAsBinary(configuration)
        else -> throw PrimitiveWriterException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun Char.writeCharAsNumber(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.N(toString())
}
private fun Char.writeCharAsString(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.S(toString())
}
// private fun Char.writeCharAsBinary(configuration: DynamapConfiguration): AttributeValue {
//
// }