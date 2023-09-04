rootProject.name = "pork"

include(
  ":common",
  ":ast",
  ":parser",
  ":frontend",
  ":evaluator",
  ":tool"
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
