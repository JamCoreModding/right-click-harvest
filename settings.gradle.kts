pluginManagement {
    repositories {
        val mavenUrls = listOf(
            "https://maven.minecraftforge.net/"
        )

        for (url in mavenUrls) {
            maven(url = url)
        }

        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("versions.toml"))
        }
    }
}
