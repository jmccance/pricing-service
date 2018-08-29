import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.2.61"
    id("io.spring.dependency-management") version "1.0.5.RELEASE"
}

repositories {
    jcenter()
}

dependencyManagement {
    imports {
        mavenBom("org.glassfish.jersey:jersey-bom:2.27")
        mavenBom("org.glassfish.grizzly:grizzly-bom:2.4.3")
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("ch.qos.logback", "logback-core", "1.2.3")
    implementation("com.fasterxml.jackson.core", "jackson-core", "2.9.6")
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.9.6")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.+")
    implementation("com.fasterxml.jackson.jaxrs", "jackson-jaxrs-json-provider", "2.9.+")
    implementation("com.fasterxml.jackson.jaxrs", "jackson-jaxrs-xml-provider", "2.9.+")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.9.+")
    implementation("com.typesafe", "config", "1.3.2")
    implementation("io.github.microutils", "kotlin-logging", "1.5.9")
    implementation("io.swagger.core.v3", "swagger-jaxrs2", "2.0.0")
    implementation("javax.servlet", "javax.servlet-api", "4.0.1")
    implementation("net.logstash.logback", "logstash-logback-encoder", "5.2")
    implementation("org.glassfish.jersey.containers", "jersey-container-grizzly2-http")
    implementation("org.glassfish.jersey.inject", "jersey-hk2")

    testImplementation("org.jetbrains.spek", "spek-api", "1.1.5")

    testRuntime("org.jetbrains.spek", "spek-junit-platform-engine", "1.1.5")
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", "5.2.0")
}

application {
    mainClassName = "pricing.MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        // Allow JSR-305 annotations to guide the Kotlin compiler's inference of
        // nulls.
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
        javaParameters = true
    }
}

tasks {
    val test by getting(Test::class) {
        useJUnitPlatform {
            includeEngines("spek")
        }
    }
}