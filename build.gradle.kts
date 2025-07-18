plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.theprogmatheus.mc.plugin.spigot"
version = "1.0-SNAPSHOT"

val mainClass = "${group}.plugintemplate.PluginTemplate"
val pluginName = project.name
val pluginVersion = project.version
val pluginAuthors = listOf("Sr_Edition", "TheProgMatheus")
val apiVersion = "1.20"
val pluginWebsite = "https://github.com/theprogmatheus/PluginTemplate"
val pluginDescription = "Um template base para desenvolvimento de plugins"

repositories {
    mavenCentral()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT") // Aikar Commands Framework
    implementation("com.j256.ormlite:ormlite-jdbc:6.1") // ORMLite dependency to Databases
    implementation("javax.inject:javax.inject:1") // JSR330 API javax.inject dependency

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.9.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "main" to mainClass,
                "name" to pluginName,
                "version" to pluginVersion,
                "apiVersion" to apiVersion,
                "authors" to pluginAuthors.joinToString("\n  - "),
                "website" to pluginWebsite,
                "description" to pluginDescription
            )
        }
    }

    shadowJar {
        dependsOn(test)
        mustRunAfter(test)
        relocate("co.aikar", "com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.acf")
        relocate("com.j256.ormlite", "com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.ormlite")
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        failFast = true
    }

    build {
        dependsOn(shadowJar)
    }
}