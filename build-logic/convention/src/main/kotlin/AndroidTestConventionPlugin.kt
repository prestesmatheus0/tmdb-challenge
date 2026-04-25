import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("testImplementation", libs.findLibrary("junit").get())
                add("testImplementation", libs.findLibrary("mockk").get())
                add("testImplementation", libs.findLibrary("turbine").get())
                add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
                add("testImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
                add("testImplementation", libs.findLibrary("robolectric").get())

                add("androidTestImplementation", libs.findLibrary("androidx-test-runner").get())
                add("androidTestImplementation", libs.findLibrary("androidx-test-ext-junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
                add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
                add("androidTestImplementation", libs.findLibrary("mockk-android").get())
                add("androidTestImplementation", libs.findLibrary("turbine").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
            }
        }
    }
}
