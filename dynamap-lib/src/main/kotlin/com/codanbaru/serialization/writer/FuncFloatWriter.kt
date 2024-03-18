package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

internal fun Float.writeFloat(type: DynamoType, configuration: DynamapConfiguration): AttributeValue {
    return when (type) {
        DynamoType.NUMBER -> writeFloatAsNumber(configuration)
        DynamoType.STRING -> writeFloatAsString(configuration)
        // DynamoType.BINARY -> writeFloatAsBinary(configuration)
        else -> throw PrimitiveWriterException.UnsupportedType(
            value = this,
            type = type,
            supportedTypes = listOf(DynamoType.NUMBER, DynamoType.STRING /*, DynamoType.BINARY */)
        )
    }
}

private fun Float.writeFloatAsNumber(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.N(toString())
}
private fun Float.writeFloatAsString(configuration: DynamapConfiguration): AttributeValue {
    // TODO: Is this supporting negative values?

    return AttributeValue.S(toString())
}
// private fun Float.writeFloatAsBinary(configuration: DynamapConfiguration): AttributeValue {
//
// }