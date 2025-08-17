plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.theprogmatheus.mc.hunters"
version = "1.0-SNAPSHOT"

val pluginPackage = "${group}.hskills"
val pluginMain = "${pluginPackage}.HSkills"
val pluginName = project.name
val pluginVersion = project.version
val pluginAuthors = listOf("Sr_Edition", "TheProgMatheus")
val apiVersion = "1.20"
val pluginWebsite = "https://github.com/theprogmatheus/HSkills"
val pluginDescription = "Sistema de habilidades com níveis, pontos de upgrade e recompensas, utilizando experiência global compartilhada entre todas as skills."
val pluginLibraries = listOf<String>() // Repository: https://repo.papermc.io/

repositories {
    mavenCentral()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation("com.zaxxer:HikariCP:7.0.1") {
        isTransitive = false
    }

    if (pluginLibraries.isNotEmpty()) {
        pluginLibraries.forEach { dependency ->
            compileOnly(dependency)
            runtimeOnly(dependency)
            testImplementation(dependency)
        }
    }

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.9.0")
    testImplementation("org.xerial:sqlite-jdbc:3.50.3.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "main" to pluginMain,
                "name" to pluginName,
                "version" to pluginVersion,
                "apiVersion" to apiVersion,
                "authors" to pluginAuthors.joinToString("\n  - "),
                "website" to pluginWebsite,
                "description" to pluginDescription,
                "libraries" to if (pluginLibraries.isEmpty()) "[]" else pluginLibraries.joinToString(
                    separator = "\n  - ",
                    prefix = "\n  - "
                ),
            )

        }
    }

    shadowJar {
        dependsOn(test)
        mustRunAfter(test)
        relocate("com.zaxxer.hikari", "${pluginPackage}.lib.hikari")
    }

    test {
        dependsOn(processResources)
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