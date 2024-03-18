package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun Long.writeLong(type: DynamoType, configuration: DynamapConfiguration): AttributeValue {
    return when (type) {
        DynamoType.NUMBER -> writeLongAsNumber(configuration)
        DynamoType.STRING -> writeLongAsString(configuration)
        // DynamoType.BINARY -> writeLongAsBinary(configuration)
        else -> throw PrimitiveWriterException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun Long.writeLongAsNumber(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.N(toString())
}
private fun Long.writeLongAsString(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.S(toString())
}
// private fun Long.writeLongAsBinary(configuration: DynamapConfiguration): AttributeValue {
// }