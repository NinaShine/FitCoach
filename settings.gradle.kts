pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // ✅ Corrige le mode de résolution
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FitCoach"
include(":app")
