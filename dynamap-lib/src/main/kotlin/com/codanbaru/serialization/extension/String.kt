package com.codanbaru.serialization.extension

internal fun String.subproperty(property: String): String = "$this.$property"