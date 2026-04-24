plugins {
    alias(libs.plugins.ifood.android.library)
}

android {
    namespace = "com.ifood.challenge.movies.core.testing"
}

dependencies {
    api(project(":core:common"))

    api(libs.junit)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
    api(libs.koin.test)
    api(libs.koin.test.junit4)
}
