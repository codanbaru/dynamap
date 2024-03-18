package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun Short.writeShort(type: DynamoType, configuration: DynamapConfiguration): AttributeValue {
    return when (type) {
        DynamoType.NUMBER -> writeShortAsNumber(configuration)
        DynamoType.STRING -> writeShortAsString(configuration)
        // DynamoType.BINARY -> writeShortAsBinary(configuration)
        else -> throw PrimitiveWriterException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun Short.writeShortAsNumber(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.N(toString())
}
private fun Short.writeShortAsString(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.S(toString())
}
// private fun Short.writeShortAsBinary(configuration: DynamapConfiguration): AttributeValue {
//
// }