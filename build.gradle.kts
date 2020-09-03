buildscript {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    maven("http://developer.huawei.com/repo/")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:4.1.0-rc02")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["kotlinVersion"]}")
    classpath("com.google.dagger:hilt-android-gradle-plugin:${properties["daggerVersion"]}")
    classpath("com.huawei.agconnect:agcp:${properties["agcpVersion"]}")
  }
}

tasks.register<Delete>("clean") {
  delete(buildDir)
}