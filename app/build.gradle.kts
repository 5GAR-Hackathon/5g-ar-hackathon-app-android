plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
  id("com.huawei.agconnect")
}

android {
  compileSdkVersion(29)

  defaultConfig {
    applicationId = "de.nanogiants.a5garapp"
    minSdkVersion(26)
    targetSdkVersion(29)
    versionCode = 1
    versionName = "1.0"

    buildConfigField("String", "BASE_URL", "\"https://5gar.vercel.app/api/\"")
  }
  signingConfigs {
    getByName("debug") {
      storeFile = file("../debug.keystore")
      storePassword = findProperty("5gar-storepass") as String? ?: "android"
      keyAlias = findProperty("5gar-keyalias") as String? ?: "androiddebug"
      keyPassword = findProperty("5gar-keypass") as String? ?: "android"
    }
  }
  buildTypes {
    getByName("release") {
      isShrinkResources = false
      isMinifyEnabled = false
    }
  }
  sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
    freeCompilerArgs = listOf("-Xjvm-default=all")
  }
  buildFeatures {
    viewBinding = true
    aidl = false
    renderScript = false
    resValues = false
    shaders = false
  }
}

repositories {
  google()
  mavenCentral()
  jcenter()
  maven("https://developer.huawei.com/repo/")
  maven("https://jitpack.io")
}

dependencies {
  // Kotlin
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

  // AndroidX
  implementation("androidx.appcompat:appcompat:1.3.0-alpha02")
  implementation("androidx.core:core-ktx:1.5.0-alpha02")
  implementation("androidx.activity:activity:1.2.0-alpha08")
  implementation("androidx.fragment:fragment:1.3.0-alpha08")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-alpha07")
  implementation("androidx.recyclerview:recyclerview:1.2.0-alpha05")
  implementation("androidx.constraintlayout:constraintlayout:2.0.1")

  // UI
  implementation("com.google.android.material:material:1.3.0-alpha02")
  implementation("io.coil-kt:coil:1.0.0-rc1")
  implementation("me.zhanghai.android.materialratingbar:library:1.4.0")
  implementation("com.github.stfalcon:stfalcon-imageviewer:1.0.1")

  // Misc
  implementation("com.jakewharton.timber:timber:4.7.1")
  implementation("io.github.serpro69:kotlin-faker:1.4.1")
  implementation("com.github.lelloman:android-identicons:v11")

  // Web
  implementation("com.squareup.moshi:moshi:1.10.0")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.10.0")
  implementation("com.squareup.okhttp3:okhttp:4.8.1")
  implementation("com.squareup.okhttp3:logging-interceptor:4.8.1")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

  // Dagger
  implementation("com.google.dagger:hilt-android:${properties["daggerVersion"]}")
  kapt("com.google.dagger:hilt-android-compiler:${properties["daggerVersion"]}")

  // Huawei
  implementation("com.huawei.hms:arenginesdk:2.12.0.1")
  implementation("com.huawei.hms:location:5.0.0.301")
  implementation("com.huawei.hms:maps:5.0.1.300")
  implementation("com.huawei.hms:site:5.0.1.300")
  implementation("com.huawei.agconnect:agconnect-core:${properties["agcpVersion"]}")
}

kapt {
  correctErrorTypes = true
}