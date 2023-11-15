plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":frontend"))
  implementation(project(":common"))
}
