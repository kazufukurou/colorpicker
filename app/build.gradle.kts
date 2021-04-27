plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdkVersion(Sdk.compile)

  buildFeatures {
    viewBinding = true
  }

  defaultConfig {
    applicationId = "com.artyommironov.colorpickersample"
    minSdkVersion(14)
    targetSdkVersion(Sdk.target)
    versionCode = 1
    versionName = "1.0"
  }
}

dependencies {
  implementation(project(":colorpicker"))
  implementation(Libs.kotlinStdLib)
}
