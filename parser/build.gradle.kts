plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":ast"))

  implementation(project(":common"))
}
