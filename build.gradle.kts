plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.7.+"
    id("io.github.p03w.machete") version "1.+"
    id("org.cadixdev.licenser") version "0.6.1"
}

apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/main/publishing.gradle.kts")
apply(from = "https://raw.githubusercontent.com/JamCoreModding/Gronk/main/misc.gradle.kts")

val mod_version: String by project

group = "io.github.jamalam360"
version = mod_version

repositories {
    val mavenUrls = mapOf(
        Pair("https://maven.terraformersmc.com/releases", listOf("com.terraformersmc")),
        Pair("https://api.modrinth.com/maven/", listOf("maven.modrinth")),
        Pair("https://maven.jamalam.tech/releases", listOf("io.github.jamalam360")),
        Pair("https://maven.quiltmc.org/repository/release", listOf("org.quiltmc"))
    )

    for (mavenPair in mavenUrls) {
        maven {
            url = uri(mavenPair.key)
            content {
                for (group in mavenPair.value) {
                    includeGroup(group)
                }
            }
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.quilt.mappings) { classifier("intermediary-v2") })

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    modImplementation(libs.required.jamlib)
    modImplementation(libs.optional.mod.menu)

    modRuntimeOnly(libs.runtime.lazy.dfu)
}

loom {
    runs {
        create("gametest") {
            server()
            name("Game Test")
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
            runDir("build/gametest")
        }

        create("gametestDebug") {
            client()
            name("Game Test")
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
            runDir("build/gametest")
        }
    }
}

tasks {
    test {
        dependsOn("runGametest")
    }
}
