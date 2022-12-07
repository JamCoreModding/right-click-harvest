plugins {
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.+"
    id("io.github.p03w.machete") version "1.+"
    id("org.cadixdev.licenser") version "0.6.+"
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
        Pair("https://maven.quiltmc.org/repository/release", listOf("org.quiltmc")),
        Pair("https://ladysnake.jfrog.io/artifactory/mods", listOf("dev.onyxstudios.cardinal-components-api")),
        Pair("https://jitpack.io", listOf("com.github.p03w-rehost")),
        Pair("https://server.bbkr.space/artifactory/libs-release", listOf("io.github.cottonmc")),
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

    modImplementation(libs.bundles.fabric)
    modApi(libs.bundles.required)
    modImplementation(libs.bundles.optional)
    modRuntimeOnly(libs.bundles.runtime)
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

