plugins {
  id("gay.pizza.pork.module")
  id("gay.pizza.pork.ast")
}

tasks.compileKotlin {
  dependsOn(tasks.generateAstCode)
}
