package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.extension.subproperty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal class DynamoListCompositeDecoder(
    private val `object`: List<AttributeValue>,

    override val property: String,

    override val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule
): DynamoCompositeDecoder(property, configuration, serializersModule) {
    override fun <T> decodeElement(descriptor: SerialDescriptor, index: Int, builder: (List<Annotation>, SerialDescriptor, String, AttributeValue) -> T): T {
        val elementAnnotations = annotationsAtIndex(descriptor, index)
        val elementDescriptor = descriptorAtIndex(descriptor, index)
        val elementName = propertyAtIndex(descriptor, index)
        val element = elementAtIndex(descriptor, index)

        return builder(elementAnnotations, elementDescriptor, elementName, element)
    }

    private var currentIndex = 0
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (currentIndex >= `object`.size) return CompositeDecoder.DECODE_DONE

        // if (currentIndex >= descriptor.elementsCount) {
        //     return CompositeDecoder.UNKNOWN_NAME
        // }
        //
        // try { descriptor.getElementName(currentIndex) } catch (throwable: Throwable) { return CompositeDecoder.UNKNOWN_NAME }

        val currentIndex = this.currentIndex
        this.currentIndex += 1
        return currentIndex
    }

    private fun annotationsAtIndex(descriptor: SerialDescriptor, index: Int): List<Annotation> =
        descriptor.getElementAnnotations(index)

    private fun propertyAtIndex(descriptor: SerialDescriptor, index: Int): String =
        "$index"

    private fun elementAtIndex(descriptor: SerialDescriptor, index: Int): AttributeValue {
        var element = `object`.getOrNull(index)
        if (element == null && configuration.evaluateUndefinedAttributesAsNullAttribute) {
            element = AttributeValue.Null(true)
        }

        return element ?: throw DynamapSerializationException.UnexpectedUndefined(property.subproperty("$index"))
    }

    private fun descriptorAtIndex(descriptor: SerialDescriptor, index: Int): SerialDescriptor =
        descriptor.getElementDescriptor(index)

}
