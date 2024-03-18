package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.dynamodb.DynamoType

internal interface PrimitiveReader {
    fun readBoolean(value: AttributeValue, type: DynamoType): Boolean
    fun readByte(value: AttributeValue, type: DynamoType): Byte
    fun readChar(value: AttributeValue, type: DynamoType): Char
    fun readShort(value: AttributeValue, type: DynamoType): Short
    fun readInt(value: AttributeValue, type: DynamoType): Int
    fun readLong(value: AttributeValue, type: DynamoType): Long
    fun readFloat(value: AttributeValue, type: DynamoType): Float
    fun readDouble(value: AttributeValue, type: DynamoType): Double
    fun readString(value: AttributeValue, type: DynamoType): String
}
