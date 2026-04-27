plugins {
    alias(libs.plugins.ifood.kotlin.library)
    alias(libs.plugins.movies.koin)
}

dependencies {
    api(project(":domain:movies:public"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.paging.common)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}
