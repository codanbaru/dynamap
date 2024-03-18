package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readLong(type: DynamoType, configuration: DynamapConfiguration): Long {
    return when (type) {
        DynamoType.NUMBER -> readLongAsNumber(configuration)
        DynamoType.STRING -> readLongAsString(configuration)
        // DynamoType.BINARY -> readLongAsBinary(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun AttributeValue.readLongAsNumber(configuration: DynamapConfiguration): Long {
    when (this) {
        is AttributeValue.N -> return value.toLong() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}
private fun AttributeValue.readLongAsString(configuration: DynamapConfiguration): Long {
    when (this) {
        is AttributeValue.S -> return value.toLong() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
// private fun AttributeValue.readLongAsBinary(configuration: DynamapConfiguration): Long {
//
// }