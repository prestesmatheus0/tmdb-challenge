plugins {
    alias(libs.plugins.ifood.android.library)
    alias(libs.plugins.ifood.koin)
}

android {
    namespace = "com.ifood.challenge.movies.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
