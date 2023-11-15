plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":execution"))
  api(project(":bytecode"))
  api(project(":compiler"))

  implementation(project(":common"))
}
