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
  implementation("androidx.constraintlayout:constraintlayout:2.0.0")

  // UI
  implementation("com.google.android.material:material:1.3.0-alpha02")

  // Misc
  implementation("com.jakewharton.timber:timber:4.7.1")

  // Web
  implementation("com.squareup.moshi:moshi:1.9.3")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.3")
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
  implementation("com.huawei.agconnect:agconnect-core:${properties["agcpVersion"]}")
}

kapt {
  correctErrorTypes = true
}