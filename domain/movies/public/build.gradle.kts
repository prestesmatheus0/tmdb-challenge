plugins {
    alias(libs.plugins.ifood.kotlin.library)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
}
