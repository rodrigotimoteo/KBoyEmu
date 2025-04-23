plugins {
    kotlin("jvm") version "1.9.23"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

group = "com.github"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}