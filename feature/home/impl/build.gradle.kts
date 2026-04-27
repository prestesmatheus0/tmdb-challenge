plugins {
    alias(libs.plugins.ifood.android.feature)
    alias(libs.plugins.ifood.android.test)
}

android {
    namespace = "com.ifood.challenge.movies.feature.home.internal"
}

dependencies {
    api(project(":feature:home:public"))

    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:network:public"))
    implementation(project(":domain:movies:public"))

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.coil.compose)

    testImplementation(libs.androidx.paging.testing)
    testImplementation(project(":core:testing"))
}
