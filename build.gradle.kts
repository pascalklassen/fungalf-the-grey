import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
}

group = "io.github.pascalklassen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral() // for transitive dependencies
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:4.3.0_301")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}