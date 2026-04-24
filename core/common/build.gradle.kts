plugins {
    alias(libs.plugins.ifood.android.library)
}

android {
    namespace = "com.ifood.challenge.movies.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
