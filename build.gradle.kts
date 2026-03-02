plugins {
    java
    id("java-library")
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.intern.hub.library"
version = "2.0.6"
description = "common"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.2")
        mavenBom("io.opentelemetry:opentelemetry-bom:1.59.0")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework:spring-context")
    api("tools.jackson.core:jackson-databind:3.1.0")

    api("io.opentelemetry:opentelemetry-api")
    api("org.springframework.boot:spring-boot-starter-opentelemetry")
    api("io.opentelemetry:opentelemetry-sdk-trace")

    implementation("org.springframework:spring-web")
    implementation("org.slf4j:slf4j-api")
    implementation("jakarta.servlet:jakarta.servlet-api")
    implementation("org.springframework:spring-webmvc")

    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}