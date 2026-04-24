plugins {
    alias(libs.plugins.ifood.android.library)
    alias(libs.plugins.movies.koin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ifood.challenge.movies.data.movies.internal"
}

dependencies {
    api(project(":data:movies:public"))
    api(project(":domain:movies:public"))

    implementation(project(":core:common"))
    implementation(project(":core:network:public"))
    implementation(project(":core:network:impl"))
    implementation(project(":core:database:public"))
    implementation(project(":core:database:impl"))

    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.robolectric)
}
