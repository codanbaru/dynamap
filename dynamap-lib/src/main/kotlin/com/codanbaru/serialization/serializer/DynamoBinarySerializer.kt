package com.codanbaru.serialization.serializer

import com.codanbaru.serialization.DynamoDecoder
import com.codanbaru.serialization.DynamoEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object DynamoBinarySerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DynamoBinarySerializer", PrimitiveKind.BYTE)

    override fun deserialize(decoder: Decoder): ByteArray {
        return when(decoder) {
            is DynamoDecoder -> decoder.decodeBinary()
            else -> throw SerializationException("DynamoBinarySerializer can be used with Dynamo serializer only!")
        }
    }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        return when(encoder) {
            is DynamoEncoder -> encoder.encodeBinary(value)
            else -> throw SerializationException("DynamoBinarySerializer can be used with Dynamo serializer only!")
        }
    }
}
