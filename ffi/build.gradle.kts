plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":frontend"))
  api(project(":evaluator"))

  implementation(project(":common"))
  implementation("net.java.dev.jna:jna:5.13.0")
}
