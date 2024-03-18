package com.codanbaru.serialization

public fun Dynamap(from: Dynamap = Dynamap.Default, builderAction: DynamapBuilder.() -> Unit): Dynamap {
    val builder = DynamapBuilder(from)
    builder.builderAction()
    val conf = builder.build()
    return Dynamap.Impl(conf, builder.serializersModule)
}