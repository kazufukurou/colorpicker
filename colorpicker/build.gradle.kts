plugins {
  id("com.android.library")
  kotlin("android")
  id("maven-publish")
}

setupDependencyUpdates()

android {
  compileSdkVersion(Sdk.compile)

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
  implementation(Libs.kotlinStdLib)
  implementation(Libs.androidxKtx)
  implementation(Libs.androidxAnnotation)
  testImplementation(Libs.kotlinTestJunit)
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
