rootProject.name = "pork"

includeBuild("buildext")

include(
  ":common",
  ":tokenizer",
  ":ast",
  ":parser",
  ":frontend",
  ":evaluator",
  ":stdlib",
  ":ffi",
  ":tool",
  ":minimal",
  ":support:pork-idea"
)
