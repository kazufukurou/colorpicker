enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("versions/android.versions.toml"))
    }
  }
}

include(":colorpicker", ":app")
rootProject.name = "Colorpicker"
