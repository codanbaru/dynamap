package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun Boolean.writeBoolean(type: DynamoType, configuration: DynamapConfiguration): AttributeValue {
    return when (type) {
        DynamoType.BOOLEAN -> writeBooleanAsBoolean(configuration)
        DynamoType.STRING -> writeBooleanAsString(configuration)
        else -> throw PrimitiveWriterException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.BOOLEAN, DynamoType.STRING)
        )
    }
}

private fun Boolean.writeBooleanAsBoolean(configuration: DynamapConfiguration): AttributeValue {
    return AttributeValue.Bool(this)
}
private fun Boolean.writeBooleanAsString(configuration: DynamapConfiguration): AttributeValue {
    val yesLiteral = configuration.booleanLiteral.yes
    val noLiteral = configuration.booleanLiteral.no

    return AttributeValue.S(
        when (this) {
            true -> yesLiteral
            false -> noLiteral
        }
    )
}
