plugins {
    alias(libs.plugins.ifood.kotlin.library)
}

dependencies {
    api(project(":domain:movies:public"))
    implementation(libs.kotlinx.coroutines.core)
}
