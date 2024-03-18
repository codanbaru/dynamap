package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readShort(type: DynamoType, configuration: DynamapConfiguration): Short {
    return when (type) {
        DynamoType.NUMBER -> readShortAsNumber(configuration)
        DynamoType.STRING -> readShortAsString(configuration)
        // DynamoType.BINARY -> readShortAsBinary(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun AttributeValue.readShortAsNumber(configuration: DynamapConfiguration): Short {
    when (this) {
        is AttributeValue.N -> return value.toShort() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}
private fun AttributeValue.readShortAsString(configuration: DynamapConfiguration): Short {
    when (this) {
        is AttributeValue.S -> return value.toShort() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
// private fun AttributeValue.readShortAsBinary(configuration: DynamapConfiguration): Short {
//
// }