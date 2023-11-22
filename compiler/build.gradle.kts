plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":ast"))
  api(project(":bir"))
  api(project(":bytecode"))
  api(project(":parser"))
  api(project(":frontend"))
  implementation(project(":common"))
}
