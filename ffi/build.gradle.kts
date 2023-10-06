plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":frontend"))
  api(project(":evaluator"))

  implementation(project(":common"))
}
