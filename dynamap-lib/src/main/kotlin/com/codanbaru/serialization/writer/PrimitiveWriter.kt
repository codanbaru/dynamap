package com.codanbaru.serialization.writer

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue

import com.codanbaru.serialization.dynamodb.DynamoType

internal interface PrimitiveWriter {
    fun writeBoolean(value: Boolean, type: DynamoType): AttributeValue
    fun writeByte(value: Byte, type: DynamoType): AttributeValue
    fun writeChar(value: Char, type: DynamoType): AttributeValue
    fun writeShort(value: Short, type: DynamoType): AttributeValue
    fun writeInt(value: Int, type: DynamoType): AttributeValue
    fun writeLong(value: Long, type: DynamoType): AttributeValue
    fun writeFloat(value: Float, type: DynamoType): AttributeValue
    fun writeDouble(value: Double, type: DynamoType): AttributeValue
    fun writeString(value: String, type: DynamoType): AttributeValue
}
