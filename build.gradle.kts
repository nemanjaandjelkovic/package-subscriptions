import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
    id("net.linguica.maven-settings") version "0.5"
}

group = "com.digitalchargingsolutions.middleware"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

val azureArtifactsContextUrl: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("${azureArtifactsContextUrl}/libs-release/maven/v1")
        name = ("libs-release")
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
    maven {
        url = uri("${azureArtifactsContextUrl}/libs-snapshot/maven/v1")
        name = ("libs-snapshot")
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
}

dependencies {
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.digitalchargingsolutions.middleware:starter-azure-service-bus:3.3.1")
    implementation("com.digitalchargingsolutions.middleware:oi-api-client:1.2.14") {
        exclude(group = "org.slf4j", module = "slf4j-reload4j")
    }
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
