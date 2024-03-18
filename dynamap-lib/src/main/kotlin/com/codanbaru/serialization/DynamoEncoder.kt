package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.dynamodb.DynamoType
import com.codanbaru.serialization.extension.dynamoTypeAnnotation
import com.codanbaru.serialization.writer.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal class DynamoEncoder(
    private val property: String,

    private val desiredType: DynamoType?,

    private val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule,

    private val consumer: (AttributeValue) -> Unit
) : Encoder {
    private val writer: PrimitiveWriter = DynamoPrimitiveWriter(configuration)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return when (descriptor.kind) {
            PrimitiveKind.BOOLEAN -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.BYTE -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.CHAR -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.DOUBLE -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.FLOAT -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.INT -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.LONG -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.SHORT -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PrimitiveKind.STRING -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)

            PolymorphicKind.OPEN -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            PolymorphicKind.SEALED -> beginStructureOnPolymorphicDescriptorKind()

            SerialKind.CONTEXTUAL -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
            SerialKind.ENUM -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind) // CHECK: Should we allow structured enums?

            StructureKind.CLASS -> beginStructureOnMapDescriptorKind()
            StructureKind.LIST -> beginStructureOnListDescriptorKind()
            StructureKind.MAP -> beginStructureOnMapDescriptorKind()
            StructureKind.OBJECT -> throw DynamapSerializationException.InvalidKind(property, descriptor.kind)
        }
    }

    private fun beginStructureOnListDescriptorKind(): CompositeEncoder {
        val dynamoType = desiredType ?: DynamoType.LIST
        return when (dynamoType) {
            DynamoType.LIST -> {
                DynamoListCompositeEncoder(property, configuration, serializersModule) { attributeValues ->
                    consumer(AttributeValue.L(attributeValues)) /* CHECK: Should raise exception in consumer is already called? */
                }
            }
            DynamoType.BINARY_SET -> {
                DynamoListCompositeEncoder(property, configuration, serializersModule) { attributeValues ->
                    val binaryValues = attributeValues.map { it.asBOrNull() ?: throw DynamapSerializationException.SetInvalid(property) }

                    consumer(AttributeValue.Bs(binaryValues)) /* CHECK: Should raise exception in consumer is already called? */
                }
            }
            DynamoType.NUMBER_SET -> {
                DynamoListCompositeEncoder(property, configuration, serializersModule) { attributeValues ->
                    val numberValues = attributeValues.map { it.asNOrNull() ?: throw DynamapSerializationException.SetInvalid(property) }

                    consumer(AttributeValue.Ns(numberValues)) /* CHECK: Should raise exception in consumer is already called? */
                }
            }
            DynamoType.STRING_SET -> {
                DynamoListCompositeEncoder(property, configuration, serializersModule) { attributeValues ->
                    val stringValues = attributeValues.map { it.asSOrNull() ?: throw DynamapSerializationException.SetInvalid(property) }

                    consumer(AttributeValue.Ss(stringValues)) /* CHECK: Should raise exception in consumer is already called? */
                }
            }
            else -> throw DynamapSerializationException.UnsupportedType(
                property = property,
                type = dynamoType,
                value = "<STRUCTURE>",
                supportedTypes = listOf(DynamoType.LIST, DynamoType.BINARY_SET, DynamoType.NUMBER_SET, DynamoType.STRING_SET)
            )
        }
    }

    private fun beginStructureOnMapDescriptorKind(): CompositeEncoder {
        val dynamoType = desiredType ?: DynamoType.MAP
        return when(dynamoType) {
            DynamoType.MAP -> {
                DynamoMapCompositeEncoder(property, configuration, serializersModule) { consumer(AttributeValue.M(it)) /* CHECK: Should raise exception in consumer is already called? */ }
            }
            else -> throw DynamapSerializationException.UnsupportedType(
                property = property,
                type = dynamoType,
                value = "<STRUCTURE>",
                supportedTypes = listOf(DynamoType.MAP)
            )
        }
    }

    private fun beginStructureOnPolymorphicDescriptorKind(): CompositeEncoder {
        val dynamoType = desiredType ?: DynamoType.MAP
        return when (dynamoType) {
            DynamoType.MAP -> {
                DynamoPolymorphicMapCompositeEncoder(property, configuration, serializersModule) { consumer(AttributeValue.M(it)) /* CHECK: Should raise exception in consumer is already called? */ }
            }
            else -> throw DynamapSerializationException.UnsupportedType(
                property = property,
                type = dynamoType,
                value = "<STRUCTURE>",
                supportedTypes = listOf(DynamoType.MAP)
            )
        }
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        return super.beginCollection(descriptor, collectionSize)
    }

    override fun encodeBoolean(value: Boolean) = handlePrimitiveException(property) { consumer(writer.writeBoolean(value, type = desiredType ?: DynamoType.BOOLEAN)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeByte(value: Byte) = handlePrimitiveException(property) { consumer(writer.writeByte(value, type = desiredType ?: DynamoType.NUMBER)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeChar(value: Char) = handlePrimitiveException(property) { consumer(writer.writeChar(value, type = desiredType ?: DynamoType.NUMBER)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeShort(value: Short) = handlePrimitiveException(property) { consumer(writer.writeShort(value, type = desiredType ?: DynamoType.NUMBER)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeInt(value: Int) = handlePrimitiveException(property) { consumer(writer.writeInt(value, type = desiredType ?: DynamoType.NUMBER)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeLong(value: Long) = handlePrimitiveException(property) { consumer(writer.writeLong(value, type = desiredType ?: DynamoType.NUMBER)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeFloat(value: Float) = handlePrimitiveException(property) { consumer(writer.writeFloat(value, type = desiredType ?: DynamoType.NUMBER)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeDouble(value: Double) = handlePrimitiveException(property) { consumer(writer.writeDouble(value, type = desiredType ?: DynamoType.NUMBER)) } /* CHECK: Should raise exception in consumer is already called? */
    override fun encodeString(value: String) = handlePrimitiveException(property) { consumer(writer.writeString(value, type = desiredType ?: DynamoType.STRING)) } /* CHECK: Should raise exception in consumer is already called? */

    fun encodeBinary(value: ByteArray) {
        consumer(AttributeValue.B(value))
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        val element = enumDescriptor.getElementName(index)

        consumer(handlePrimitiveException(property) { writer.writeString(element, type = desiredType ?: DynamoType.STRING) })
    }

    override fun encodeNull() {
        consumer(AttributeValue.Null(true)) /* CHECK: Should raise exception in consumer is already called? */
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        if (descriptor.elementsCount == 1) {
            val elementAnnotation = descriptor.getElementAnnotations(0)
            // val elementDescriptor = descriptor.getElementDescriptor(0)
            // val elementName = descriptor.getElementName(0)

            val dynamoType = desiredType ?: elementAnnotation.dynamoTypeAnnotation

            return DynamoEncoder(property, dynamoType, /* annotations + elementAnnotation, elementDescriptor, */ configuration, serializersModule) { consumer(it) }  /* CHECK: Should raise exception in consumer is already called? */
        }

        throw DynamapSerializationException.InlineInvalid(property)
    }

    override fun encodeNotNullMark() {
        // DO NOTHING
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        serializer.serialize(this, value)
    }

    override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
        val isNullabilitySupported = serializer.descriptor.isNullable
        if (isNullabilitySupported) {
            // Instead of `serializer.serialize` to be able to intercept this
            return encodeSerializableValue(serializer as SerializationStrategy<T?>, value)
        }

        // Else default path used to avoid allocation of NullableSerializer
        if (value == null) {
            encodeNull()
        } else {
            encodeNotNullMark()
            encodeSerializableValue(serializer, value)
        }
    }
}