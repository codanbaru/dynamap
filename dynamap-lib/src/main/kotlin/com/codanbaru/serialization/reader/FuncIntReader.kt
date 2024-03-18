package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readInt(type: DynamoType, configuration: DynamapConfiguration): Int {
    return when (type) {
        DynamoType.NUMBER -> readIntAsNumber(configuration)
        DynamoType.STRING -> readIntAsString(configuration)
        // DynamoType.BINARY -> readIntAsBinary(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun AttributeValue.readIntAsNumber(configuration: DynamapConfiguration): Int {
    when (this) {
        is AttributeValue.N -> return value.toInt() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}
private fun AttributeValue.readIntAsString(configuration: DynamapConfiguration): Int {
    when (this) {
        is AttributeValue.S -> return value.toInt() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
// private fun AttributeValue.readIntAsBinary(configuration: DynamapConfiguration): Int {
//
// }
