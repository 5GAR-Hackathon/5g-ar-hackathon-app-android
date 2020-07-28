plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("dagger.hilt.android.plugin")
}

android {
  compileSdkVersion(30)
  buildToolsVersion = "30.0.1"

  defaultConfig {
    applicationId = "de.nanogiants.a5garapp"
    minSdkVersion(26)
    targetSdkVersion(30)
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    getByName("release") {
      isShrinkResources = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  maven("https://kotlin.bintray.com/kotlinx")
}

val daggerVersion: String by project
dependencies {
  // Kotlin
  implementation(kotlin("stdlib-jdk8"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.8-1.4.0-rc")

  // AndroidX
  implementation("androidx.appcompat:appcompat:1.3.0-alpha01")
  implementation("androidx.core:core-ktx:1.5.0-alpha01")
  implementation("androidx.activity:activity:1.2.0-alpha07")
  implementation("androidx.fragment:fragment:1.3.0-alpha07")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-alpha06")
  implementation("androidx.recyclerview:recyclerview:1.2.0-alpha05")
  implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta8")

  // UI
  implementation("com.google.android.material:material:1.3.0-alpha02")

  // Misc
  implementation("com.jakewharton.timber:timber:4.7.1")

  // Dagger
  implementation("com.google.dagger:hilt-android:$daggerVersion")
  kapt("com.google.dagger:hilt-android-compiler:$daggerVersion")
}

kapt {
  correctErrorTypes = true
}