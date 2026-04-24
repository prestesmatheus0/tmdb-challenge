plugins {
    alias(libs.plugins.ifood.android.feature)
    alias(libs.plugins.ifood.android.test)
}

android {
    namespace = "com.ifood.challenge.movies.feature.detail.internal"
}

dependencies {
    api(project(":feature:detail:public"))

    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:network:public"))
    implementation(project(":domain:movies:public"))

    implementation(libs.coil.compose)

    testImplementation(project(":core:testing"))
}
