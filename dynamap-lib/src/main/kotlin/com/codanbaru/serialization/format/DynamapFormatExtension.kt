package com.codanbaru.serialization.format

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.serialization.serializer

public inline fun <reified T> DynamapFormat.encodeToItem(value: T): Map<String, AttributeValue> =
    encodeToAttribute(serializersModule.serializer(), value).asM()
public inline fun <reified T> DynamapFormat.encodeToAttribute(value: T): AttributeValue =
    encodeToAttribute(serializersModule.serializer(), value)

public inline fun <reified T> DynamapFormat.decodeFromItem(item: Map<String, AttributeValue>): T =
    decodeFromAttribute(serializersModule.serializer(), AttributeValue.M(item))
public inline fun <reified T> DynamapFormat.decodeFromAttribute(value: AttributeValue): T =
    decodeFromAttribute(serializersModule.serializer(), value)
