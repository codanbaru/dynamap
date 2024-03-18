package com.codanbaru.serialization.annotation

import com.codanbaru.serialization.dynamodb.DynamoType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class SerialType(val type: DynamoType)
