plugins {
    alias(libs.plugins.ifood.android.library)
    alias(libs.plugins.ifood.koin)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ifood.challenge.movies.core.database.internal"
}

dependencies {
    api(project(":core:database:public"))

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.robolectric)
}
