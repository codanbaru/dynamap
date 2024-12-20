val serializationCoreVersion: String = "1.6.3"
val dynamoDbVersion: String = "1.0.69"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")

    id("maven-publish")
    id("tech.yanand.maven-central-publish") version "1.3.0"

    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationCoreVersion")
    implementation("aws.sdk.kotlin:dynamodb:$dynamoDbVersion")
    testImplementation(kotlin("test-junit5"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}


publishing {
    publications {
        create<MavenPublication>("MavenJava") {
            groupId = "com.codanbaru.kotlin"
            artifactId = "dynamap"
            version = project.findProperty("lib.version") as String? ?: "0"

            from(components["java"])

            pom {
                name = "Dynamap"
                description = "Library to serialize and deserialize documents from DynamoDB using kotlinx.serialization."
                url = "https://github.com/codanbaru/dynamap"

                licenses {
                    license {
                        name = "GPL-v3.0"
                        url = "http://www.gnu.org/licenses/gpl-3.0.txt"
                    }
                }

                developers {
                    developer {
                        id = "diegofer"
                        name = "Diego Fernandez"
                        email = "diego@diegofer.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/codanbaru/dynamap.git"
                    developerConnection = "scm:git:ssh://github.com:codanbaru/dynamap.git"
                    url = "https://github.com/codanbaru/dynamap"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["MavenJava"])
}

mavenCentral {
    authToken = project.findProperty("sonartype.central.token") as String? ?: ""

    publishingType = "USER_MANAGED"

    maxWait = 120
}
