package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.dynamodb.DynamoType
import com.codanbaru.serialization.writer.DynamoPrimitiveWriter
import com.codanbaru.serialization.writer.PrimitiveWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal class DynamoPolymorphicMapCompositeEncoder(
    private val property: String,

    private val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule,

    private val consumer: (Map<String, AttributeValue>) -> Unit
): CompositeEncoder {
    private var typeAttribute: Pair<String, AttributeValue>? = null
    private var mapAttribute: Map<String, AttributeValue>? = null

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean = true

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        val elementName = descriptor.getElementName(index)
        if (elementName != "type") throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)

        val discriminator = configuration.classDiscriminator.ifBlank { "type" }

        typeAttribute = discriminator to AttributeValue.S(value)
    }

    override fun <T> encodeSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T) {
        val elementName = descriptor.getElementName(index)
        if (elementName != "value") throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)

        val encoder = DynamoEncoder(property, DynamoType.MAP, configuration, serializersModule) {
            mapAttribute = when(it) {
                is AttributeValue.M -> it.value
                else -> throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
            }
        }
        serializer.serialize(encoder, value)
    }

    override fun <T : Any> encodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) {
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        val typeAttribute = this.typeAttribute
        val mapAttribute = this.mapAttribute

        if (typeAttribute == null) throw DynamapSerializationException.PolymorphicInvalid.Uncompleted(property = property)
        if (mapAttribute == null) throw DynamapSerializationException.PolymorphicInvalid.Uncompleted(property = property)

        if (mapAttribute[typeAttribute.first] != null && mapAttribute[typeAttribute.first] != typeAttribute.second) throw DynamapSerializationException.PolymorphicInvalid.DiscriminatorCollision(property = property)

        val attributes = mapAttribute.toMutableMap()
        attributes[typeAttribute.first] = typeAttribute.second
        consumer(attributes.toMap())
    }
}