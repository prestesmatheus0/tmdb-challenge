import java.util.Properties

plugins {
    alias(libs.plugins.ifood.android.application)
    alias(libs.plugins.ifood.android.compose)
    alias(libs.plugins.ifood.koin)
    alias(libs.plugins.ifood.android.test)
    alias(libs.plugins.kotlin.serialization)
}

val tmdbApiKey: String = run {
    val props = Properties()
    val localProps = rootProject.file("local.properties")
    if (localProps.exists()) {
        localProps.inputStream().use { props.load(it) }
    }
    props.getProperty("TMDB_API_KEY")
        ?: System.getenv("TMDB_API_KEY")
        ?: ""
}

android {
    namespace = "com.ifood.challenge.movies"

    defaultConfig {
        applicationId = "com.ifood.challenge.movies"
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
        buildConfigField(
            "String",
            "TMDB_BASE_URL",
            "\"https://api.themoviedb.org/3/\"",
        )
        buildConfigField(
            "String",
            "TMDB_IMAGE_BASE_URL",
            "\"https://image.tmdb.org/t/p/\"",
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            "META-INF/LICENSE.md",
            "META-INF/LICENSE-notice.md",
        )
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    implementation(project(":core:network:impl"))
    implementation(project(":core:network:public"))
    implementation(project(":core:database:impl"))
    implementation(project(":core:database:public"))

    implementation(project(":data:movies:impl"))
    implementation(project(":data:movies:public"))
    implementation(project(":domain:movies:impl"))
    implementation(project(":domain:movies:public"))

    implementation(project(":feature:home:impl"))
    implementation(project(":feature:home:public"))
    implementation(project(":feature:detail:impl"))
    implementation(project(":feature:detail:public"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
}
