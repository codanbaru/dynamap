package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readFloat(type: DynamoType, configuration: DynamapConfiguration): Float {
    return when (type) {
        DynamoType.NUMBER -> readFloatAsNumber(configuration)
        DynamoType.STRING -> readFloatAsString(configuration)
        // DynamoType.BINARY -> readFloatAsBinary(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun AttributeValue.readFloatAsNumber(configuration: DynamapConfiguration): Float {
    when (this) {
        is AttributeValue.N -> return value.toFloat() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}
private fun AttributeValue.readFloatAsString(configuration: DynamapConfiguration): Float {
    when (this) {
        is AttributeValue.S -> return value.toFloat() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
// private fun AttributeValue.readFloatAsBinary(configuration: DynamapConfiguration): Float {
//
// }