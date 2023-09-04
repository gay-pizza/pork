rootProject.name = "pork"

include(
  ":ast",
  ":core"
)

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      version("clikt", "4.2.0")

      library("clikt", "com.github.ajalt.clikt", "clikt")
        .versionRef("clikt")
    }
  }
}
