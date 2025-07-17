rootProject.name = "pork"

includeBuild("buildext")

include(
  ":common",
  ":tokenizer",
  ":ast",
  ":bir",
  ":bytecode",
  ":parser",
  ":frontend",
  ":compiler",
  ":execution",
  ":vm",
  ":evaluator",
  ":stdlib",
  ":ffi",
  ":tool",
  ":minimal",
  ":support:pork-idea",
)
