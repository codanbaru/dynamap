package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun Byte.writeByte(type: DynamoType, configuration: DynamapConfiguration): AttributeValue {
    return when (type) {
        DynamoType.NUMBER -> writeByteAsNumber(configuration)
        DynamoType.STRING -> writeByteAsString(configuration)
        // DynamoType.BINARY -> writeByteAsBinary(configuration)
        else -> throw PrimitiveWriterException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun Byte.writeByteAsNumber(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.N(toString())
}
private fun Byte.writeByteAsString(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.S(toString())
}
// private fun Byte.writeByteAsBinary(configuration: DynamapConfiguration): AttributeValue {
//
// }