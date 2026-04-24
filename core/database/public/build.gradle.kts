plugins {
    alias(libs.plugins.ifood.android.library)
}

android {
    namespace = "com.ifood.challenge.movies.core.database"
}

dependencies {
    api(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.common)
    implementation(libs.kotlinx.coroutines.core)
}
