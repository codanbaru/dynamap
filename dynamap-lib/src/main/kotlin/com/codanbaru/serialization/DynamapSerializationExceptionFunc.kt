package com.codanbaru.serialization

import com.codanbaru.serialization.reader.PrimitiveReaderException
import com.codanbaru.serialization.writer.PrimitiveWriterException

fun <T> handlePrimitiveException(property: String, block: () -> T): T {
    try {
        return block()
    } catch (exception: Throwable) {
        if (exception is DynamapSerializationException) throw exception
        if (exception is PrimitiveReaderException) {
            when (exception) {
                is PrimitiveReaderException.Generic -> throw DynamapSerializationException.Exception(property, exception)
                is PrimitiveReaderException.UnexpectedType -> throw DynamapSerializationException.UnexpectedType(property, exception.value, exception.type)
                is PrimitiveReaderException.UnsupportedType -> throw DynamapSerializationException.UnsupportedType(property, exception.value, exception.type, exception.supportedTypes)
            }
        }
        if (exception is PrimitiveWriterException) {
            when (exception) {
                is PrimitiveWriterException.Generic -> throw DynamapSerializationException.Exception(property, exception)
                is PrimitiveWriterException.UnsupportedType -> throw DynamapSerializationException.UnsupportedType(property, exception.value, exception.type, exception.supportedTypes)
            }
        }

        throw DynamapSerializationException.Exception(property, cause = exception)
    }
}
