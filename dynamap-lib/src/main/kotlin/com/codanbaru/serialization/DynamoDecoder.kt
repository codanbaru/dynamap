package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.dynamodb.DynamoType
import com.codanbaru.serialization.extension.dynamoTypeAnnotation
import com.codanbaru.serialization.reader.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal class DynamoDecoder(
    private val attributeValue: AttributeValue,

    private val property: String,

    private val desiredType: DynamoType?,

    private val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule
): Decoder {
    private val reader: PrimitiveReader = DynamoPrimitiveReader(configuration)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
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

    private fun beginStructureOnListDescriptorKind(): CompositeDecoder {
        val dynamoType = desiredType ?: DynamoType.LIST
        val values = when (dynamoType) {
            DynamoType.LIST -> {
                attributeValue.asLOrNull() ?: throw DynamapSerializationException.UnexpectedType(
                    property = property,
                    type = dynamoType,
                    value = attributeValue
                )
            }
            DynamoType.BINARY_SET -> {
                attributeValue.asBsOrNull()?.map { AttributeValue.B(it) } ?: throw DynamapSerializationException.UnexpectedType(
                    property = property,
                    type = dynamoType,
                    value = attributeValue
                )
            }
            DynamoType.NUMBER_SET -> {
                attributeValue.asNsOrNull()?.map { AttributeValue.N(it) } ?: throw DynamapSerializationException.UnexpectedType(
                    property = property,
                    type = dynamoType,
                    value = attributeValue
                )
            }
            DynamoType.STRING_SET -> {
                attributeValue.asSsOrNull()?.map { AttributeValue.S(it) } ?: throw DynamapSerializationException.UnexpectedType(
                    property = property,
                    type = dynamoType,
                    value = attributeValue
                )
            }
            else -> throw DynamapSerializationException.UnsupportedType(
                property = property,
                type = dynamoType,
                value = attributeValue,
                supportedTypes = listOf(DynamoType.LIST, DynamoType.BINARY_SET, DynamoType.NUMBER_SET, DynamoType.STRING_SET)
            )
        }

        return DynamoListCompositeDecoder(values, property, configuration, serializersModule)
    }

    private fun beginStructureOnMapDescriptorKind(): CompositeDecoder {
        val dynamoType = desiredType ?: DynamoType.MAP
        val values = when(dynamoType) {
            DynamoType.MAP -> {
                attributeValue.asMOrNull() ?: throw DynamapSerializationException.UnexpectedType(
                    property = property,
                    type = dynamoType,
                    value = attributeValue
                )
            }
            else -> throw DynamapSerializationException.UnsupportedType(
                property = property,
                type = dynamoType,
                value = attributeValue,
                supportedTypes = listOf(DynamoType.MAP)
            )
        }

        return DynamoMapCompositeDecoder(values, property, configuration, serializersModule)
    }

    private fun beginStructureOnPolymorphicDescriptorKind(): CompositeDecoder {
        val dynamoType = desiredType ?: DynamoType.MAP
        val values = when(dynamoType) {
            DynamoType.MAP -> {
                attributeValue.asMOrNull() ?: throw DynamapSerializationException.UnexpectedType(
                    property = property,
                    type = dynamoType,
                    value = attributeValue
                )
            }
            else -> throw DynamapSerializationException.UnsupportedType(
                property = property,
                type = dynamoType,
                value = attributeValue,
                supportedTypes = listOf(DynamoType.MAP)
            )
        }

        return DynamoPolymorphicMapCompositeDecoder(values, property, configuration, serializersModule)
    }

    override fun decodeBoolean(): Boolean = handlePrimitiveException(property) { reader.readBoolean(attributeValue, type = desiredType ?: DynamoType.BOOLEAN) }
    override fun decodeByte(): Byte = handlePrimitiveException(property) { reader.readByte(attributeValue, type = desiredType ?: DynamoType.NUMBER) }
    override fun decodeChar(): Char = handlePrimitiveException(property) { reader.readChar(attributeValue, type = desiredType ?: DynamoType.NUMBER) }
    override fun decodeShort(): Short = handlePrimitiveException(property) { reader.readShort(attributeValue, type = desiredType ?: DynamoType.NUMBER) }
    override fun decodeInt(): Int = handlePrimitiveException(property) { reader.readInt(attributeValue, type = desiredType ?: DynamoType.NUMBER) }
    override fun decodeLong(): Long = handlePrimitiveException(property) { reader.readLong(attributeValue, type = desiredType ?: DynamoType.NUMBER) }
    override fun decodeFloat(): Float = handlePrimitiveException(property) { reader.readFloat(attributeValue, type = desiredType ?: DynamoType.NUMBER) }
    override fun decodeDouble(): Double = handlePrimitiveException(property) { reader.readDouble(attributeValue, type = desiredType ?: DynamoType.NUMBER) }
    override fun decodeString(): String = handlePrimitiveException(property) { reader.readString(attributeValue, type = desiredType ?: DynamoType.STRING) }

    fun decodeBinary(): ByteArray {
        return when(attributeValue) {
            is AttributeValue.B -> attributeValue.value
            else -> throw DynamapSerializationException.UnexpectedType(
                property = property,
                type = DynamoType.BINARY,
                value = attributeValue
            )
        }
    }

    override fun decodeNull(): Nothing? {
        return when(attributeValue) {
            is AttributeValue.Null -> null
            else -> throw DynamapSerializationException.UnexpectedType(
                property = property,
                type = DynamoType.NULL,
                value = attributeValue
            )
        }
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val element = handlePrimitiveException(property) { reader.readString(attributeValue, type = desiredType ?: DynamoType.STRING) }
        val elementIndex = enumDescriptor.getElementIndex(element)

        if (elementIndex in IntRange(0, enumDescriptor.elementsCount - 1)) {
            return elementIndex
        } else {
            throw DynamapSerializationException.EnumInvalid(property, element, enumDescriptor.elementNames.toList())
        }
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder {
        if (descriptor.elementsCount == 1) {
            val elementAnnotation = descriptor.getElementAnnotations(0)
            // val elementDescriptor = descriptor.getElementDescriptor(0)
            // val elementName = descriptor.getElementName(0)

            val dynamoType = desiredType ?: elementAnnotation.dynamoTypeAnnotation

            return DynamoDecoder(attributeValue, property, dynamoType, configuration, serializersModule)
        }

        throw DynamapSerializationException.InlineInvalid(property)
    }

    override fun decodeNotNullMark(): Boolean {
        return when(attributeValue) {
            is AttributeValue.Null -> false
            else -> true
        }
    }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        deserializer.deserialize(this)

    override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T?  {
        val isNullabilitySupported = deserializer.descriptor.isNullable

        return if (isNullabilitySupported || decodeNotNullMark()) decodeSerializableValue(deserializer) else decodeNull()
    }
}