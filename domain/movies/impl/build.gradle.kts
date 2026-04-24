plugins {
    alias(libs.plugins.ifood.kotlin.library)
}

dependencies {
    api(project(":domain:movies:public"))

    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.paging.common)

    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
