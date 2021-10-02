import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.5.30"
}

group = "io.github.pascalklassen"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.github.pascalklassen.fungalf.LauncherKt")
}

repositories {
    mavenCentral() // for transitive dependencies
    maven {
        name = "ossrh"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
    jcenter()
}

dependencies {
    // -- kotlin --
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // -- discord api --
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:1.5.0-SNAPSHOT")
    // -- exposed --
    implementation("org.jetbrains.exposed:exposed-core:0.34.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.34.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.34.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.34.1")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("mysql:mysql-connector-java:8.0.19")
    // -- http client --
    implementation("io.ktor:ktor-client-core:1.6.4")
    implementation("io.ktor:ktor-client-jackson:1.6.4")
    implementation("io.ktor:ktor-client-logging:1.6.4")
    // -- logging --
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.10")
    // -- utilities --
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("io.github.pascalklassen:poke-future:0.1.0-SNAPSHOT")
    // -- vertx kotlin --
    implementation("io.vertx:vertx-lang-kotlin:4.1.2")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:4.1.2")
    // -- configuration
    implementation("com.uchuhimo:konf:1.1.2")
    // -- testing --
    testImplementation(kotlin("test"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    test {
        useJUnit()
    }

    installDist {
        destinationDir = buildDir.resolve("libs/install")
    }
}
