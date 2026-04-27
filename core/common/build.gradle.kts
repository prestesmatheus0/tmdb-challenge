plugins {
    alias(libs.plugins.ifood.android.library)
    alias(libs.plugins.movies.koin.android)
}

android {
    namespace = "com.ifood.challenge.movies.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}
