plugins {
    java
    id("org.springframework.boot") version "3.2.5" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("io.freefair.lombok") version "8.6" apply false
    jacoco
}

allprojects {
    group = "com.cosmoscan"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = BigDecimal(0.60)
                }
            }
        }
    }
}

// Определяем версии здесь, чтобы они были доступны всем модулям
ext {
    set("springCloudVersion", "2023.0.1")
    set("lombokVersion", "1.18.32")
    set("kumoVersion", "1.28")
    set("pdfboxVersion", "3.0.0")
    set("poiVersion", "5.2.5")
    set("testcontainersVersion", "1.19.6")
}