import nu.studer.gradle.jooq.JooqEdition
import org.jooq.util.jaxb.tools.XMLAppendable
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("groovy")
    id("idea")
    id("nu.studer.jooq") version "10.1"
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.liquibase.gradle") version "2.2.2"
    id("jacoco")
}

group = "me.modkzl"

repositories {
    mavenCentral()
}

val jooqVersion = "3.19.8"
val h2Version = "2.3.232"
val liquibaseVersion = "4.20.0"
val spockVersion = "2.4-M1-groovy-4.0"

dependencies {
    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("org.projectlombok:lombok")

    // jOOQ dependencies
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-meta:$jooqVersion")
    implementation("org.jooq:jooq-codegen:$jooqVersion")

    // Liquibase for database migrations
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // HTTP client for external service calls
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4")

    // H2 Database for local development
    runtimeOnly("com.h2database:h2:$h2Version")

    jooqGenerator("com.h2database:h2:$h2Version")
    jooqGenerator("org.jooq:jooq:$jooqVersion")
    jooqGenerator("org.jooq:jooq-meta:$jooqVersion")
    jooqGenerator("org.jooq:jooq-codegen:$jooqVersion")

    // Liquibase runtime dependencies
    liquibaseRuntime("org.liquibase:liquibase-core:$liquibaseVersion")
    liquibaseRuntime("com.h2database:h2:$h2Version")
    // Needed for liquibase to run tasks, despite Spring having built-in Logback
    liquibaseRuntime("ch.qos.logback:logback-core")
    liquibaseRuntime("ch.qos.logback:logback-classic")
    liquibaseRuntime("info.picocli:picocli:4.6.1")

    // Spock for testing
    testImplementation("org.spockframework:spock-core:$spockVersion")
    testImplementation("org.spockframework:spock-spring:$spockVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // Lombok for reducing boilerplate code
    annotationProcessor("org.projectlombok:lombok")

    "developmentOnly"("org.springframework.boot:spring-boot-devtools")
}

jooq {
    version.set(jooqVersion)
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration {
                jdbc {
                    driver = "org.h2.Driver"
                    url = "jdbc:h2:file:./db/liquibase.h2;AUTO_SERVER=TRUE"
                    user = "sa"
                    password = "password"
                }
                generator {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database {
                        name = "org.jooq.meta.h2.H2Database"
                        includes = ".*"
                        excludes = "DATABASECHANGELOG|DATABASECHANGELOGLOCK"
                    }
                    target {
                        packageName = "me.modkzl.jooq"
                        directory = "build/generated-src/jooq/main"
                    }
                    generate {
                        isFluentSetters = true
                    }
                }
            }
        }
    }
}

liquibase {
    activities.register("core") {
        this.arguments = mapOf(
            "changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.yaml",
            "url" to "jdbc:h2:./db/liquibase.h2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "username" to "sa",
            "password" to "password"
        )
    }
}

idea {
    module {
        testSources.from(file("src/test/groovy"))
    }
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude("**/me/modkzl/service/**\$*") // Exclude synthetic classes
                }
            }
        )
    )
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    args("--spring.profiles.active=local")
}

val versionFile = file("version.txt")

fun incrementVersion(version: String): String {
    val parts = version.trim().split(".").map { it.toInt() }.toMutableList()
    parts[2] = parts[2] + 1
    return parts.joinToString(".")
}

tasks.withType<BootJar> {
    doFirst {
        val currentVersion = if (versionFile.exists()) versionFile.readText().trim() else "1.0.0"
        val newVersion = incrementVersion(currentVersion)
        versionFile.writeText(newVersion)
        archiveFileName.set("payment-service-$newVersion.jar")
    }
}

operator fun <T : XMLAppendable> T.invoke(block: T.() -> Unit) = this.apply(block)