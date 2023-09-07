rootProject.name = "pork"

includeBuild("buildext")

include(
  ":common",
  ":ast",
  ":parser",
  ":frontend",
  ":evaluator",
  ":stdlib",
  ":ffi",
  ":tool"
)
