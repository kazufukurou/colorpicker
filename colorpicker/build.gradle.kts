plugins {
  id("com.android.library")
  kotlin("android")
  id("maven-publish")
}

android {
  compileSdkVersion(30)

  defaultConfig {
    minSdkVersion(14)
  }

  buildFeatures {
    buildConfig = false
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}

dependencies {
  implementation(libs.kotlinStdlib)
  implementation(libs.androidxCoreKtx)
  implementation(libs.androidxAnnotation)
  testImplementation(libs.kotlinTestJunit)
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("release") {
        from(components.findByName("release"))
      }
    }
  }
}
