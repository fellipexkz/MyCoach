pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
            setUrl("https://maven.scijava.org/content/repositories/public/")
        }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
            setUrl("https://maven.scijava.org/content/repositories/public/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/public/")
        }
    }
}

rootProject.name = "MyCoach"
include(":app")