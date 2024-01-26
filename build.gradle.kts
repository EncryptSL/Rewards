plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.encryptsl.rewards"
version = providers.gradleProperty("plugin_version").get()
description = providers.gradleProperty("plugin_description").get()

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", "1.9.22"))
    compileOnly("com.zaxxer:HikariCP:5.1.0")
    compileOnly("com.discordsrv:discordsrv:1.27.0")
    compileOnly("org.jetbrains.exposed:exposed-core:0.46.0")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:0.46.0")
    compileOnly("org.jetbrains.exposed:exposed-kotlin-datetime:0.44.1")
    implementation("dev.triumphteam:triumph-gui:3.1.7")
    implementation("cloud.commandframework:cloud-paper:2.0.0-SNAPSHOT")
    implementation("cloud.commandframework:cloud-annotations:2.0.0-SNAPSHOT")
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
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
    shadowJar {
        minimize {
            relocate("cloud.commandframework", "cloud-core")
            relocate("dev.triumphteam.gui", "triumpteam")
        }
    }
}

kotlin {
    jvmToolchain(17)
}