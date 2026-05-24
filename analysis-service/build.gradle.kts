plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${rootProject.ext.get("springCloudVersion")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    runtimeOnly("com.h2database:h2")

    implementation("org.apache.pdfbox:pdfbox:${rootProject.ext.get("pdfboxVersion")}")
    implementation("org.apache.poi:poi-ooxml:${rootProject.ext.get("poiVersion")}")
    implementation("com.kennycason:kumo-core:${rootProject.ext.get("kumoVersion")}")

    implementation(project(":common-dto"))

    implementation("io.github.resilience4j:resilience4j-spring-boot2:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:${rootProject.ext.get("testcontainersVersion")}")
    testImplementation("org.testcontainers:junit-jupiter:${rootProject.ext.get("testcontainersVersion")}")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.bootJar {
    archiveFileName.set("analysis-service.jar")
}