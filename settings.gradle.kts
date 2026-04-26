pluginManagement {
    includeBuild("build-logic")
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
    }
}

rootProject.name = "ifood-challenge-movies"

include(":app")

include(":core:common")
include(":core:designsystem")
include(":core:network:public")
include(":core:network:impl")
include(":core:database:impl")
include(":core:testing")

include(":data:movies:public")
include(":data:movies:impl")

include(":domain:movies:public")
include(":domain:movies:impl")

include(":feature:home:public")
include(":feature:home:impl")
include(":feature:detail:public")
include(":feature:detail:impl")
