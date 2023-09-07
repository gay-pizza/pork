rootProject.name = "pork"

includeBuild("buildext")

include(
  ":common",
  ":ast",
  ":parser",
  ":frontend",
  ":evaluator",
  ":ffi",
  ":tool"
)
