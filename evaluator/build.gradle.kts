plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":ast"))
  api(project(":execution"))
  api(project(":frontend"))

  implementation(project(":common"))
}
