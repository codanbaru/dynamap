package com.codanbaru.serialization.reader

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.codanbaru.serialization.dynamodb.DynamoType
import com.codanbaru.serialization.writer.PrimitiveWriterException

public sealed class PrimitiveReaderException(
    open val value: Any,
    open val type: DynamoType,
    override val message: String? = null,
    override val cause: Throwable? = null,
): Throwable(message, cause) {
    public class Generic(
        override val value: Any,
        override val type: DynamoType,
        override val message: String?,
        override val cause: Throwable?,
    ): PrimitiveReaderException(value, type, message, cause)

    public class UnexpectedType(
        override val value: Any,
        override val type: DynamoType
    ): PrimitiveReaderException(
        value = value,
        type = type,
        message = "Unable to read '$value' value as '$type' dynamo type."
    )

    public class UnsupportedType(
        override val value: Any,
        override val type: DynamoType,
        public val supportedTypes: List<DynamoType>
    ): PrimitiveReaderException(
        value = value,
        type = type,
        message = "Unable to read '$value' value as '$type' dynamo type. Supported types: ${ supportedTypes.joinToString { "'$it'" } }."
    )
}