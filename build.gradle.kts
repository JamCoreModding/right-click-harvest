import com.matthewprenger.cursegradle.CurseProject
import java.util.*
import org.gradle.jvm.tasks.Jar

plugins {
    id("eclipse")
    id("maven-publish")
    id("net.minecraftforge.gradle") version "5.1.+"
    id("io.github.p03w.machete") version "1.+"
    id("org.cadixdev.licenser") version "0.6.+"
    id("com.modrinth.minotaur") version "2.+"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.github.breadmoirai.github-release") version "2.4.1"
}

val mod_version: String by project
val archive_base_name: String by project

group = "io.github.jamalam360"

version = mod_version

base { archivesBaseName = archive_base_name }

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

println(
        "Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}"
)

minecraft {
    mappings("official", "1.19.4")

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "rightclickharvest")

            mods { create("rightclickharvest") { source(sourceSets.getByName("main")) } }
        }

        create("server") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "rightclickharvest")

            mods { create("rightclickharvest") { source(sourceSets.getByName("main")) } }
        }
    }
}

dependencies { minecraft(libs.minecraft) }

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.jamalam360"
            artifactId = project.property("archive_base_name") as String

            if (getGradleProperty("mod_version") != null) {
                version = getGradleProperty("mod_version")
            } else {
                println("version not found in gradle.properties")
            }

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "JamalamMavenRelease"
            url = uri("https://maven.jamalam.tech/releases")
            credentials {
                username = System.getenv()["MAVEN_USERNAME"]!!
                password = System.getenv()["MAVEN_PASSWORD"]!!
            }
        }
    }
}

curseforge {
    apiKey = System.getenv()["CURSEFORGE_API_KEY"]!!

    project(
            closureOf<CurseProject> {
                id = getGradleProperty("curseforge_project_id")!!
                changelogType = "markdown"
                releaseType =
                        if (getGradleProperty("mod_version")!!.contains("beta")) "beta"
                        else "release"
                changelog = project.rootProject.file("CHANGELOG.md")
                mainArtifact(tasks.get("jar"))
                mainArtifact.displayName = getGradleProperty("release_name")!!

                afterEvaluate { uploadTask.dependsOn("jar") }

                addGameVersion("Forge")

                getGradleProperty("supported_versions")!!.split(",").forEach { addGameVersion(it) }
            }
    )
}

modrinth {
    versionNumber.set(getGradleProperty("mod_version")!!)
    versionName.set(getGradleProperty("release_name")!!)
    versionType.set(if (getGradleProperty("mod_version")!!.contains("beta")) "beta" else "release")
    token.set(System.getenv()["MODRINTH_API_KEY"])
    projectId.set(getGradleProperty("modrinth_project_id")!!)
    uploadFile.set(tasks.get("jar"))
    gameVersions.addAll(getGradleProperty("supported_versions")!!.split(","))
    loaders.addAll(listOf("forge"))
    changelog.set(project.rootProject.file("CHANGELOG.md").readText())
}

githubRelease {
    token(System.getenv()["GITHUB_TOKEN"])
    owner(getGradleProperty("github_user"))
    repo(getGradleProperty("github_repo"))
    tagName(getGradleProperty("mod_version"))
    releaseName(getGradleProperty("release_name"))
    body(project.rootProject.file("CHANGELOG.md").readText())
    prerelease(getGradleProperty("mod_version")!!.contains("beta"))
    draft(false)

    if (getGradleProperty("release_branch") != null) {
        targetCommitish(getGradleProperty("release_branch"))
    }

    val libsDir = project.file("build/libs")
    val devLibsDir = project.file("build/devLibs")

    if (libsDir.exists() && devLibsDir.exists()) {
        val archiveBaseName = getGradleProperty("archive_base_name")!!
        val libs =
                libsDir.listFiles().filter {
                    it.name.endsWith(".jar") && it.name.contains(archiveBaseName)
                }
        val devLibs =
                devLibsDir.listFiles().filter {
                    it.name.endsWith(".jar") && it.name.contains(archiveBaseName)
                }
        releaseAssets(libs, devLibs)
    }
}

tasks {
    getByName("jar") { finalizedBy("reobfJar") }

    withType<Jar> { manifest { attributes["Implementation-Version"] = mod_version } }

    withType<JavaCompile> { options.encoding = "UTF-8" }

    named("publish") {
        dependsOn("jar")
        dependsOn("build")
        dependsOn("githubRelease")
        dependsOn("curseforge")
        dependsOn("modrinth")

        doLast {
            val changelog = project.rootProject.file("CHANGELOG.md")
            val changelogTemplate = project.rootProject.file("CHANGELOG_TEMPLATE.md")
            changelog.writeText(changelogTemplate.readText())
    
            val libs = project.file("build/libs").listFiles().filter { it.name.endsWith(".jar") }
            libs.forEach { it.delete() }
    
            val devLibs =
                project.file("build/devlibs").listFiles().filter { it.name.endsWith(".jar") }
            devLibs.forEach { it.delete() }
        }
    }
}

fun getGradleProperty(name: String): String? {
    return project.properties[name] as String?
}
