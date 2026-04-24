plugins {
    alias(libs.plugins.ifood.android.library)
    alias(libs.plugins.ifood.koin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ifood.challenge.movies.core.network.internal"
}

dependencies {
    api(project(":core:network:public"))

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.mockk)
}
