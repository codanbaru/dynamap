package com.codanbaru.serialization

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal class DynamoMapCompositeEncoder(
    override val property: String,

    override val configuration: DynamapConfiguration,
    override val serializersModule: SerializersModule,

    private val consumer: (Map<String, AttributeValue>) -> Unit
): DynamoCompositeEncoder(property, configuration, serializersModule) {
    private var `object`: MutableMap<String, AttributeValue> = mutableMapOf()

    override fun <T> encodeElement(descriptor: SerialDescriptor, index: Int, value: T, builder: (List<Annotation>, SerialDescriptor, String, T, (AttributeValue) -> Unit) -> Unit) {
        val elementAnnotations = annotationsAtIndex(descriptor, index)
        val elementDescriptor = descriptorAtIndex(descriptor, index)
        val elementName = propertyAtIndex(descriptor, index)

        builder(elementAnnotations, elementDescriptor, elementName, value) { `object`[elementName] = it /* CHECK: Should we raise exception if encoder is already finished? */ }
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int, builder: (List<Annotation>, SerialDescriptor, String, (AttributeValue) -> Unit) -> Encoder): Encoder {
        val elementAnnotations = annotationsAtIndex(descriptor, index)
        val elementDescriptor = descriptorAtIndex(descriptor, index)
        val elementName = propertyAtIndex(descriptor, index)

        return builder(elementAnnotations, elementDescriptor, elementName) { `object`[elementName] = it /* CHECK: Should we raise exception if encoder is already finished? */ }
    }

    override fun finish() {
        // CHECK: Should we raise exception if encoder is already finished?

        consumer(`object`.toMap())
    }

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean {
        return true
    }

    private fun annotationsAtIndex(descriptor: SerialDescriptor, index: Int): List<Annotation> =
        descriptor.getElementAnnotations(index)

    private fun propertyAtIndex(descriptor: SerialDescriptor, index: Int): String =
        descriptor.getElementName(index)

    private fun descriptorAtIndex(descriptor: SerialDescriptor, index: Int): SerialDescriptor =
        descriptor.getElementDescriptor(index)
}