plugins {
    val kotlinVersion = "1.9.20"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}

