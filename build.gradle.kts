plugins {
    id("fabric-loom") version "0.11-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.7.1"
    id("org.quiltmc.quilt-mappings-on-loom") version "4.0.0"
    id("org.cadixdev.licenser") version "0.6.1"
}

val modVersion = "2.0.0"

group = "io.github.jamalam360"
version = modVersion

repositories {
    val mavenUrls = mapOf(
        Pair("https://maven.terraformersmc.com/releases", listOf("com.terraformersmc"))
    )

    mavenLocal()

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
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:1.18.2+build.22:v2"))
    })

    modImplementation(libs.loader)
    modImplementation(libs.fabric.api) // For gametest API
}

loom {
    runs {
        this.create("gametest") {
            server()
            name("Game Test")
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
            runDir("build/gametest")
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "version" to project.version
                )
            )
        }
    }

    build {
        dependsOn("updateLicenses")
    }

    test {
        dependsOn("runGametest")
    }

    jar {
        archiveBaseName.set("rightclickharvest")
    }

    remapJar {
        archiveBaseName.set("rightclickharvest")
    }

    withType<JavaCompile> {
        options.release.set(17)
    }
}