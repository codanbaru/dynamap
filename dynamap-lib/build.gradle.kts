val serializationCoreVersion: String = "1.6.3"
val dynamoDbVersion: String = "1.0.69"

plugins {
    kotlin("jvm")

    id("maven-publish")

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
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("CiRepo"))
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.codanbaru.kotlin"
            artifactId = "dynamap"
            version = "0.8.0"

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
    sign(publishing.publications["mavenJava"])
}
