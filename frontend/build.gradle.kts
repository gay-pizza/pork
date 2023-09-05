plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":ast"))
  api(project(":parser"))

  implementation(project(":common"))
}
