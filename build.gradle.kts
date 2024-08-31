import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "com.github.encryptsl"
version = providers.gradleProperty("plugin_version").get()
description = providers.gradleProperty("plugin_description").get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.triumphteam.dev/snapshots/")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", "2.0.20"))
    compileOnly("com.zaxxer:HikariCP:5.1.0")
    compileOnly("com.discordsrv:discordsrv:1.27.0")
    compileOnly("com.github.encryptsl:KiraDiscord:1.0.6")
    compileOnly("org.jetbrains.exposed:exposed-core:0.54.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.54.0")
    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.54.0")

    implementation("com.github.encryptsl:KMonoLib:1.0.2")
    implementation("dev.triumphteam:triumph-gui-paper:4.0.0-SNAPSHOT") {
        exclude("net.kyori")
    }
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.20")
}

sourceSets {
    getByName("main") {
        java {
            srcDir("src/main/java")
        }
        kotlin {
            srcDir("src/main/kotlin")
        }
    }
}


tasks {
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
    }
    processResources {
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand(project.properties)
        }
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.compilerArgs.add("-Xlint:deprecation")
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
        minimize {
            relocate("com.github.encryptsl.kmono.lib", "kmono-lib")
            relocate("org.incendo.cloud", "cloud-core")
            relocate("dev.triumphteam.gui", "triumpteam")
        }
    }
}