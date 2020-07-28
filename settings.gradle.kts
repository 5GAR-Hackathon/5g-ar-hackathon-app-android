include("app")

pluginManagement {
    val kotlinVersion: String by settings
    val androidGradlePluginVersion: String by settings
    val daggerVersion: String by settings
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application" -> useModule("com.android.tools.build:gradle:$androidGradlePluginVersion")
                "dagger.hilt.android.plugin" -> useModule("com.google.dagger:hilt-android-gradle-plugin:$daggerVersion")
            }
            if (requested.id.namespace == "org.jetbrains.kotlin") useVersion(kotlinVersion)
        }
    }
}