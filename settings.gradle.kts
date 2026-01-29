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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = java.net.URI("https://jitpack.io"))
    }
}

rootProject.name = "ReaderApp"
include(":app")
include(":feature-auth")
include(":feature-books")
include(":feature-upload")
include(":feature-book-reader")
include(":feature-profile")
include(":core-domain")
include(":core-data")
include(":core-ui")
