plugins {
    alias(libs.plugins.ifood.android.library)
    alias(libs.plugins.movies.koin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ifood.challenge.movies.core.database.internal"
}

dependencies {
    api(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.common)
    implementation(libs.kotlinx.coroutines.core)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.robolectric)
}
