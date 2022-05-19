pluginManagement {
    repositories {
        val mavenUrls = listOf(
            "https://maven.fabricmc.net/",
            "https://maven.quiltmc.org/repository/release"
        )

        for (url in mavenUrls) {
            maven(url = url)
        }

        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("versions.toml"))
        }
    }
}