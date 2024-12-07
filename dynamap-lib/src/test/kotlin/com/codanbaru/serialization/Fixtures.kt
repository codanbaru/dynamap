package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object Fixtures {
    @Serializable
    data class Primitives(
        val string: String,
        val char: Char,
        val short: Short,
        val int: Int,
        val long: Long,
        val float: Float,
        val double: Double,
    )

    @Serializable
    data class Optionals(
        val a: Int,
        val b: Int = 1,
        val c: Int = 2
    )

    @Serializable
    data class Polymorphic(val type: Type) {

        @Serializable
        sealed interface Type {
            @Serializable
            @SerialName("a")
            data class A(val a: Int): Type

            @Serializable
            @SerialName("b")
            data class B(val b: Int): Type
        }

    }
}