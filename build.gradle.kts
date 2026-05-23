plugins {
    java
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "8.5.1"
}

group = "com.example"
version = "1.0.0"
description = "Idiomatic OpenTelemetry template for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        // Align all OpenTelemetry artifacts on the starter's versions. Imported via the
        // Spring dependency-management plugin so it overrides the older OTel core that
        // Spring Boot manages by default (otherwise the starter fails at runtime).
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.28.1")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    // OpenTelemetry: the starter auto-instruments HTTP, metrics, and logs in-process
    // (SDK, not the bytecode agent) and is configured via standard OTEL_* env vars /
    // otel.* properties — no manual SDK wiring needed.
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:all")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Spotless with google-java-format keeps formatting consistent and enforced.
spotless {
    java {
        googleJavaFormat()
        importOrder()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
