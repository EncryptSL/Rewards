plugins {
    kotlin("jvm") version "1.9.23"
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "com.github.encryptsl"
version = providers.gradleProperty("plugin_version").get()
description = providers.gradleProperty("plugin_description").get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.5-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", "1.9.23"))
    compileOnly("com.zaxxer:HikariCP:5.1.0")
    compileOnly("com.discordsrv:discordsrv:1.27.0")
    compileOnly("com.github.encryptsl.kira:KiraDiscord:1.0.0")
    compileOnly("org.jetbrains.exposed:exposed-core:0.49.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.49.0")

    implementation("dev.triumphteam:triumph-gui:3.1.7")
    implementation("org.incendo:cloud-paper:2.0.0-beta.5")
    implementation("org.incendo:cloud-annotations:2.0.0-beta.5")
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-beta.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    test {
        useJUnitPlatform()
    }
    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand(project.properties)
        }
    }
    shadowJar {
        minimize {
            relocate("org.incendo.cloud", "cloud-core")
            relocate("dev.triumphteam.gui", "triumpteam")
        }
    }
}