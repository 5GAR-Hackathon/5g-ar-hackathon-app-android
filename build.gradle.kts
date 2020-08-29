buildscript {
  val kotlin_version by extra("1.3.72")
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    maven("http://developer.huawei.com/repo/")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:${properties["androidGradlePluginVersion"]}")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["kotlinVersion"]}")
    classpath("com.google.dagger:hilt-android-gradle-plugin:${properties["daggerVersion"]}")
    classpath("com.huawei.agconnect:agcp:${properties["agcpVersion"]}")
  }
}

tasks.register<Delete>("clean") {
  delete(buildDir)
}