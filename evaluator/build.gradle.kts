plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":ast"))
  api(project(":frontend"))

  implementation(project(":common"))
}
