package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.DynamapConfiguration
import com.codanbaru.serialization.dynamodb.DynamoType

public class DynamoPrimitiveWriter(
    val configuration: DynamapConfiguration
): PrimitiveWriter {
    override fun writeBoolean(value: Boolean, type: DynamoType): AttributeValue =
        value.writeBoolean(type, configuration)
    override fun writeByte(value: Byte, type: DynamoType): AttributeValue =
        value.writeByte(type, configuration)
    override fun writeChar(value: Char, type: DynamoType): AttributeValue =
        value.writeChar(type, configuration)
    override fun writeShort(value: Short, type: DynamoType): AttributeValue =
        value.writeShort(type, configuration)
    override fun writeInt(value: Int, type: DynamoType): AttributeValue =
        value.writeInt(type, configuration)
    override fun writeLong(value: Long, type: DynamoType): AttributeValue =
        value.writeLong(type, configuration)
    override fun writeFloat(value: Float, type: DynamoType): AttributeValue =
        value.writeFloat(type, configuration)
    override fun writeDouble(value: Double, type: DynamoType): AttributeValue =
        value.writeDouble(type, configuration)
    override fun writeString(value: String, type: DynamoType): AttributeValue =
        value.writeString(type, configuration)
}
