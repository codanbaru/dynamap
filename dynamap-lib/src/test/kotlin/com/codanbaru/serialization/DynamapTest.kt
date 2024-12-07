package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.format.decodeFromItem
import com.codanbaru.serialization.format.encodeToItem
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals

class DynamapTest {
    val dynamap = Dynamap {
        classDiscriminator = "_type"
    }

    @Test
    fun `only primitives`() {
        assertCodec(
            Fixtures.Primitives(
                string = "string",
                char = 'c',
                short = 0,
                int = 1,
                long = 2,
                float = 3.0F,
                double = 4.0,
            ),
            mapOf(
                "string" to AttributeValue.S("string"),
                "char" to AttributeValue.N("c"),
                "short" to AttributeValue.N("0"),
                "int" to AttributeValue.N("1"),
                "long" to AttributeValue.N("2"),
                "float" to AttributeValue.N("3.0"),
                "double" to AttributeValue.N("4.0"),
            )
        )
    }

    @Test
    fun `optionals with all values set`() {
        assertCodec(
            Fixtures.Optionals(
                a = 1,
                b = 2,
                c = 3
            ),
            mapOf(
                "a" to AttributeValue.N("1"),
                "b" to AttributeValue.N("2"),
                "c" to AttributeValue.N("3"),
            )
        )
    }

    @Test
    fun `optionals with no optionals set`() {
        assertAll(
            {
                assertCodec(
                    Fixtures.Optionals(
                        a = 0
                    ),
                    mapOf(
                        "a" to AttributeValue.N("0"),
                        "b" to AttributeValue.N("1"),
                        "c" to AttributeValue.N("2"),
                    )
                )
            },
            {
                assertDecode(
                    Fixtures.Optionals(a = 0),
                    mapOf("a" to AttributeValue.N("0")),
                )
            }
        )
    }

    @Test
    fun `optionals with middle optionals not set`() {
        assertAll(
            {
                assertCodec(
                    Fixtures.Optionals(
                        a = 0,
                        c = 3,
                    ),
                    mapOf(
                        "a" to AttributeValue.N("0"),
                        "b" to AttributeValue.N("1"),
                        "c" to AttributeValue.N("3"),
                    )
                )
            },
            {
                assertDecode(
                    Fixtures.Optionals(a = 0, c = 3),
                    mapOf(
                        "a" to AttributeValue.N("0"),
                        "c" to AttributeValue.N("3")
                    ),
                )
            }
        )
    }

    @Test
    fun `polymorphic`() {
        assertAll(
            {
                assertCodec(
                    Fixtures.Polymorphic(type = Fixtures.Polymorphic.Type.A(1)),
                    mapOf(
                        "type" to AttributeValue.M(mapOf(
                            "_type" to AttributeValue.S("a"),
                            "a" to AttributeValue.N("1"),
                        ))
                    )
                )
            }
        )
    }

    inline fun <reified T>assertCodec(expectedObj: T, expectedItem: Map<String, AttributeValue>) {
        assertAll(
            { assertEquals(expectedItem, dynamap.encodeToItem(expectedObj), "encoding to tiem") },
            { assertDecode(expectedObj, expectedItem) },
            { assertEquals(expectedObj, dynamap.decodeFromItem(dynamap.encodeToItem(expectedObj))) },
        )
    }

    inline fun <reified T>assertDecode(expectedObj: T, actualItem: Map<String, AttributeValue>) {
        assertEquals(expectedObj, dynamap.decodeFromItem(actualItem), "decoding from item")
    }
}