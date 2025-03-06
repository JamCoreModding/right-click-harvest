import com.matthewprenger.cursegradle.CurseProject
import java.util.*
import org.gradle.jvm.tasks.Jar

plugins {
	id("eclipse")
	id("maven-publish")
	id("idea")
	id("net.minecraftforge.gradle") version "[6.0,6.2)"
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

minecraft {
	mappings("official", "1.20.1")

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

if (System.getenv()["MAVEN_USERNAME"] != null) {
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
}

if (System.getenv()["CURSEFORGE_API_KEY"] != null) {
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
				addGameVersion("NeoForge")

				getGradleProperty("supported_versions")!!.split(",").forEach { addGameVersion(it) }
			}
		)
	}
}

if (System.getenv()["MODRINTH_API_KEY"] != null) {
	modrinth {
		versionNumber.set(getGradleProperty("mod_version")!!)
		versionName.set(getGradleProperty("release_name")!!)
		versionType.set(if (getGradleProperty("mod_version")!!.contains("beta")) "beta" else "release")
		token.set(System.getenv()["MODRINTH_API_KEY"])
		projectId.set(getGradleProperty("modrinth_project_id")!!)
		uploadFile.set(tasks.get("jar"))
		gameVersions.addAll(getGradleProperty("supported_versions")!!.split(","))
		loaders.addAll(listOf("forge", "neoforge"))
		changelog.set(project.rootProject.file("CHANGELOG.md").readText())
	}
}

if (System.getenv()["GITHUB_TOKEN"] != null) {
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
	}
}

fun getGradleProperty(name: String): String? {
	return project.properties[name] as String?
}
