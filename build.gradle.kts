import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.5.21"
}

group = "io.github.pascalklassen"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.github.pascalklassen.fungalf.LauncherKt")
}

repositories {
    mavenCentral() // for transitive dependencies
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven {
        name = "ossrh"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    // -- testing --
    testImplementation(kotlin("test"))
    // -- discord api --
    implementation("net.dv8tion:JDA:4.3.0_301")
    // -- logging --
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")
    // -- utilities --
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("io.github.pascalklassen:poke-future:0.1.0-SNAPSHOT")
    // -- reactive io --
    implementation("io.vertx:vertx-core:4.1.2")
    implementation("io.vertx:vertx-lang-kotlin:4.1.2")
    implementation("io.vertx:vertx-mysql-client:4.1.2")
    // -- configuration
    implementation("com.uchuhimo:konf:1.1.2")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    test {
        useJUnit()
    }

    installDist {
        destinationDir = buildDir.resolve("libs/install")
    }
}
