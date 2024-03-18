package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readChar(type: DynamoType, configuration: DynamapConfiguration): Char {
    return when (type) {
        DynamoType.NUMBER -> readCharAsNumber(configuration)
        DynamoType.STRING -> readCharAsString(configuration)
        // DynamoType.BINARY -> readCharAsBinary(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun AttributeValue.readCharAsNumber(configuration: DynamapConfiguration): Char {
    when (this) {
        is AttributeValue.N -> return value[0] // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}
private fun AttributeValue.readCharAsString(configuration: DynamapConfiguration): Char {
    when (this) {
        is AttributeValue.S -> return value[0] // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
// private fun AttributeValue.readCharAsBinary(configuration: DynamapConfiguration): Char {
//
// }