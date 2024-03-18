package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun AttributeValue.readBoolean(type: DynamoType, configuration: DynamapConfiguration): Boolean {
    return when (type) {
        DynamoType.BOOLEAN -> readBooleanAsBoolean(configuration)
        DynamoType.STRING -> readBooleanAsString(configuration)
        else -> throw PrimitiveReaderException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.BOOLEAN, DynamoType.STRING)
        )
    }
}

private fun AttributeValue.readBooleanAsBoolean(configuration: DynamapConfiguration): Boolean {
    when (this) {
        is AttributeValue.Bool -> return value
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.BOOLEAN
        )
    }
}
private fun AttributeValue.readBooleanAsString(configuration: DynamapConfiguration): Boolean {
    when (this) {
        is AttributeValue.S -> {
            val yesLiteral = when (configuration.booleanLiteral.caseSensitive) {
                true -> configuration.booleanLiteral.yes
                false -> configuration.booleanLiteral.yes.uppercase()
            }
            val noLiteral = when (configuration.booleanLiteral.caseSensitive) {
                true -> configuration.booleanLiteral.no
                false -> configuration.booleanLiteral.no.uppercase()
            }
            if (yesLiteral == noLiteral) throw PrimitiveReaderException.Generic(this,  DynamoType.STRING, "Invalid boolean literal configuration detected. Yes literal and No literal are equal.", null)

            val value = when (configuration.booleanLiteral.caseSensitive) {
                true -> value
                false -> value.uppercase()
            }

            if (value == yesLiteral) return true
            if (value == noLiteral) return false

            throw PrimitiveReaderException.Generic(this,  DynamoType.STRING, "Invalid boolean literal received. Booleans encoded as string need to be '$yesLiteral' or '$noLiteral'.", null)
        }
        else -> throw PrimitiveReaderException.UnexpectedType(
            value = this,
            type = DynamoType.STRING
        )
    }
}
