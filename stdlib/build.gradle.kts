plugins {
  id("gay.pizza.pork.module")
}

tasks.processResources {
  from("src/main/pork") {
    into("pork/stdlib")
  }
}

dependencies {
  implementation(project(":frontend"))
}
