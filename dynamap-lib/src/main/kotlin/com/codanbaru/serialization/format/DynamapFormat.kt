package com.codanbaru.serialization.format

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy

interface DynamapFormat: SerialFormat {
    fun <T> encodeToAttribute(serializer: SerializationStrategy<T>, value: T): AttributeValue
    fun <T> decodeFromAttribute(deserializer: DeserializationStrategy<T>, value: AttributeValue): T
}
