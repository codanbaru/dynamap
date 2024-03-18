package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readByte(type: DynamoType, configuration: DynamapConfiguration): Byte {
    return when (type) {
        DynamoType.NUMBER -> readByteAsNumber(configuration)
        DynamoType.STRING -> readByteAsString(configuration)
        // DynamoType.BINARY -> readByteAsBinary(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun AttributeValue.readByteAsNumber(configuration: DynamapConfiguration): Byte {
    when (this) {
        is AttributeValue.N -> return value.toByte() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}
private fun AttributeValue.readByteAsString(configuration: DynamapConfiguration): Byte {
    when (this) {
        is AttributeValue.S -> return value.toByte() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
// private fun AttributeValue.readByteAsBinary(configuration: DynamapConfiguration): Byte {
//
// }