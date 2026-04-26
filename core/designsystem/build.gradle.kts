plugins {
    alias(libs.plugins.ifood.android.library)
    alias(libs.plugins.ifood.android.compose)
    alias(libs.plugins.ifood.android.test)
}

android {
    namespace = "com.ifood.challenge.movies.core.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
