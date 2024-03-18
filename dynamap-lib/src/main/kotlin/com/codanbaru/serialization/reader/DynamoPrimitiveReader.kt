package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

class DynamoPrimitiveReader(
    val configuration: DynamapConfiguration
): PrimitiveReader {
    override fun readBoolean(value: AttributeValue, type: DynamoType): Boolean =
        value.readBoolean(type, configuration)
    override fun readByte(value: AttributeValue, type: DynamoType): Byte =
        value.readByte(type, configuration)
    override fun readChar(value: AttributeValue, type: DynamoType): Char =
        value.readChar(type, configuration)
    override fun readShort(value: AttributeValue, type: DynamoType): Short =
        value.readShort(type, configuration)
    override fun readInt(value: AttributeValue, type: DynamoType): Int =
        value.readInt(type, configuration)
    override fun readLong(value: AttributeValue, type: DynamoType): Long =
        value.readLong(type, configuration)
    override fun readFloat(value: AttributeValue, type: DynamoType): Float =
        value.readFloat(type, configuration)
    override fun readDouble(value: AttributeValue, type: DynamoType): Double =
        value.readDouble(type, configuration)
    override fun readString(value: AttributeValue, type: DynamoType): String =
        value.readString(type, configuration)
}