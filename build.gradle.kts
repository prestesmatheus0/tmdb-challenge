plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kover)
}

dependencies {
    // Aggregate coverage across all modules into the root project's report
    kover(project(":app"))
    kover(project(":core:common"))
    kover(project(":core:designsystem"))
    kover(project(":core:network:public"))
    kover(project(":core:network:impl"))
    kover(project(":core:database:impl"))
    kover(project(":data:movies:public"))
    kover(project(":data:movies:impl"))
    kover(project(":domain:movies:public"))
    kover(project(":domain:movies:impl"))
    kover(project(":feature:home:public"))
    kover(project(":feature:home:impl"))
    kover(project(":feature:detail:public"))
    kover(project(":feature:detail:impl"))
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.BuildConfig",
                    "*.di.*Module*",
                    "*.*KoinModule*",
                    "*ComposableSingletons*",
                    "*_Factory*",
                    // Room-generated implementations (KSP)
                    "*Dao_Impl*",
                    "*Database_Impl*",
                    "*MoviesDatabase*",
                )
                annotatedBy(
                    "androidx.compose.runtime.Composable",
                    "androidx.compose.ui.tooling.preview.Preview",
                    "androidx.room.Database",
                    "androidx.room.Dao",
                    "androidx.room.Entity",
                )
                packages(
                    "*.designsystem.theme",
                    "*.designsystem.preview",
                    "*.core.database.dao",
                    "*.core.database.entity",
                    "*.core.database.internal",
                    // Out of coverage scope: infra (network/connectivity/dispatchers) — focus is use cases, VMs, repos, mappers
                    "com.ifood.challenge.movies.core.common.network",
                    "com.ifood.challenge.movies.core.common.coroutines",
                    "com.ifood.challenge.movies.core.common.di",
                    "com.ifood.challenge.movies.core.network.internal",
                    "com.ifood.challenge.movies.di",
                )
                classes(
                    "com.ifood.challenge.movies.MainActivity",
                    "com.ifood.challenge.movies.IfoodMoviesApp",
                    "com.ifood.challenge.movies.AppNavHost*",
                )
            }
        }
    }
}

val detektCompose = libs.detekt.compose

subprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)
    apply(plugin = rootProject.libs.plugins.detekt.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kover.get().pluginId)

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        ignoreFailures.set(false)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        }
        filter {
            exclude { it.file.path.contains("/build/") }
        }
    }

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        toolVersion = rootProject.libs.versions.detekt.get()
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        source.setFrom(files("src/main/kotlin", "src/main/java"))
    }

    dependencies {
        add("detektPlugins", detektCompose)
    }
}
