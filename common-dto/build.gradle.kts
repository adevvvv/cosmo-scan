plugins {
    java
    id("io.freefair.lombok")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
}

// Отключаем bootJar, так как это не Spring Boot модуль
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

// Включаем обычный jar
tasks.jar {
    enabled = true
    archiveClassifier.set("")
}