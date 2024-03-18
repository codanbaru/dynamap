package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readString(type: DynamoType, configuration: DynamapConfiguration): String {
    return when (type) {
        DynamoType.STRING -> readStringAsString(configuration)
        DynamoType.BINARY -> readStringAsBinary(configuration)
        DynamoType.NUMBER -> readStringAsNumber(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.STRING, DynamoType.BINARY, DynamoType.NUMBER)
        )
    }
}

private fun AttributeValue.readStringAsString(configuration: DynamapConfiguration): String {
    when (this) {
        is AttributeValue.S -> return value
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
private fun AttributeValue.readStringAsBinary(configuration: DynamapConfiguration): String {
    when (this) {
        is AttributeValue.B -> return value.toString(Charsets.UTF_8) // TODO: Handle exceptions
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.BINARY
        )
    }
}
private fun AttributeValue.readStringAsNumber(configuration: DynamapConfiguration): String {
    when (this) {
        // TODO: Is this working when string is not a number?
        is AttributeValue.N -> return value
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.NUMBER
        )
    }
}