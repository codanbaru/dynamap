package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.annotation.SerialType
import com.codanbaru.serialization.dynamodb.DynamoType
import com.codanbaru.serialization.extension.dynamoTypeAnnotation
import com.codanbaru.serialization.extension.subproperty
import com.codanbaru.serialization.writer.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal abstract class DynamoCompositeEncoder(
    open val property: String,

    open val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule
) : CompositeEncoder {
    private val writer: PrimitiveWriter by lazy { DynamoPrimitiveWriter(configuration) }

    abstract fun <T> encodeElement(descriptor: SerialDescriptor, index: Int, value: T, builder: (List<Annotation>, SerialDescriptor, String, T, (AttributeValue) -> Unit) -> Unit)
    abstract fun encodeInlineElement(descriptor: SerialDescriptor, index: Int, builder: (List<Annotation>, SerialDescriptor, String, (AttributeValue) -> Unit) -> Encoder): Encoder

    abstract fun finish()

    final override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeBoolean(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.BOOLEAN) }

        consumer(attributeValue)
    }
    final override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeByte(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.NUMBER) }
        
        consumer(attributeValue)
    }
    final override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeChar(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.NUMBER) }

        consumer(attributeValue)
    }
    final override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeShort(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.NUMBER) }

        consumer(attributeValue)
    }
    final override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeInt(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.NUMBER) }

        consumer(attributeValue)
    }
    final override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeLong(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.NUMBER) }

        consumer(attributeValue)
    }
    final override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeFloat(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.NUMBER) }

        consumer(attributeValue)
    }
    final override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeDouble(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.NUMBER) }

        consumer(attributeValue)
    }
    final override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val attributeValue = handlePrimitiveException(property.subproperty(elementName)) { writer.writeString(element, elementAnnotations.dynamoTypeAnnotation ?: DynamoType.STRING) }

        consumer(attributeValue)
    }

    final override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder = encodeInlineElement(descriptor, index) abstractEncodeInlineElement@ { elementAnnotations, _, elementName, consumer ->
        if (descriptor.elementsCount == 1) {
            val inlinedElementAnnotation = descriptor.getElementAnnotations(0)
            // val inlinedElementDescriptor = descriptor.getElementDescriptor(0)
            // val inlinedElementName = descriptor.getElementName(0)

            val dynamoType = elementAnnotations.dynamoTypeAnnotation ?: inlinedElementAnnotation.dynamoTypeAnnotation

            return@abstractEncodeInlineElement DynamoEncoder(property.subproperty(elementName), dynamoType, configuration, serializersModule) { consumer(it) }  /* CHECK: Should raise exception in consumer is already called? */
        }

        throw DynamapSerializationException.InlineInvalid(property)
    }

    final override fun <T> encodeSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T): Unit = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        val dynamoType = elementAnnotations.dynamoTypeAnnotation

        val encoder = DynamoEncoder(property.subproperty(elementName), dynamoType, configuration, serializersModule) { consumer(it) }
        serializer.serialize(encoder, element)
    }

    final override fun <T: Any> encodeNullableSerializableElement(descriptor: SerialDescriptor, index: Int, serializer: SerializationStrategy<T>, value: T?) = encodeElement(descriptor, index, value) { elementAnnotations, _, elementName, element, consumer ->
        if (element == null) {
            consumer(AttributeValue.Null(true))
        } else {
            val dynamoType = elementAnnotations.dynamoTypeAnnotation

            val encoder = DynamoEncoder(property.subproperty(elementName), dynamoType, configuration, serializersModule) { consumer(it) }
            serializer.serialize(encoder, element)
        }
    }

    final override fun endStructure(descriptor: SerialDescriptor) {
        finish()
    }
}