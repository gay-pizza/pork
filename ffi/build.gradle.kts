plugins {
  id("gay.pizza.pork.module")
}

dependencies {
  api(project(":frontend"))
  api(project(":evaluator"))

  implementation(project(":common"))
  implementation("com.github.jnr:jffi:1.3.14")
  implementation("com.github.jnr:jffi:1.3.14:native")
}
