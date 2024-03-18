package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readDouble(type: DynamoType, configuration: DynamapConfiguration): Double {
    return when (type) {
        DynamoType.NUMBER -> readDoubleAsNumber(configuration)
        DynamoType.STRING -> readDoubleAsString(configuration)
        // DynamoType.BINARY -> readDoubleAsBinary(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun AttributeValue.readDoubleAsNumber(configuration: DynamapConfiguration): Double {
    when (this) {
        is AttributeValue.N -> return value.toDouble() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}
private fun AttributeValue.readDoubleAsString(configuration: DynamapConfiguration): Double {
    when (this) {
        is AttributeValue.S -> return value.toDouble() // TODO: Is this supporting negative values?
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
// private fun AttributeValue.readDoubleAsBinary(configuration: DynamapConfiguration): Double {
//
// }