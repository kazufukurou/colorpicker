buildscript {
  repositories {
    google()
    jcenter()
    gradlePluginPortal()
  }
  dependencies {
    classpath(Plugins.android)
    classpath(Plugins.kotlin)
    classpath(Plugins.gradleVersions)
  }
}

allprojects {
  repositories {
    google()
    jcenter()
  }
}

val clean by tasks.creating(Delete::class) {
  delete(rootProject.buildDir)
}
