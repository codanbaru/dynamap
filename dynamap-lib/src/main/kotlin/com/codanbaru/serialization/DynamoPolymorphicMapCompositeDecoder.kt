package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.dynamodb.DynamoType
import com.codanbaru.serialization.reader.DynamoPrimitiveReader
import com.codanbaru.serialization.reader.PrimitiveReader
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal class DynamoPolymorphicMapCompositeDecoder(
    private val `object`: Map<String, AttributeValue>,

    private val property: String,

    private val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule
): CompositeDecoder {
    override fun decodeSequentially(): Boolean = false
    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = -1

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder =
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)

    private var currentDecodeElementIndex = 0
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (currentDecodeElementIndex >= descriptor.elementsCount) return CompositeDecoder.DECODE_DONE

        currentDecodeElementIndex += 1

        return currentDecodeElementIndex - 1
    }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String {
        val elementName = descriptor.getElementName(index)
        if (elementName != "type") throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)

        val discriminator = configuration.classDiscriminator.ifBlank { "type" }
        val elementAttribute = `object`[discriminator] ?: throw DynamapSerializationException.PolymorphicInvalid.DiscriminatorNotPresent(property = property)

        return elementAttribute.asSOrNull() ?: throw DynamapSerializationException.PolymorphicInvalid.DiscriminatorNotPresent(property = property)
    }

    override fun <T> decodeSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T>, previousValue: T?): T {
        val elementName = descriptor.getElementName(index)
        if (elementName != "value") throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)

        val element = AttributeValue.M(`object`)
        val decoder = DynamoDecoder(element, elementName, DynamoType.MAP, configuration, serializersModule)
        return deserializer.deserialize(decoder)
    }

    override fun <T : Any> decodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, deserializer: DeserializationStrategy<T?>, previousValue: T?): T? {
        throw DynamapSerializationException.PolymorphicInvalid.Type(property = property)
    }

    override fun endStructure(descriptor: SerialDescriptor) {

    }
}