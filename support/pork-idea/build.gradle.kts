import gay.pizza.pork.buildext.AstCodegenType

plugins {
  id("org.jetbrains.intellij.platform") version "2.1.0"
  id("gay.pizza.pork.module")
  id("gay.pizza.pork.ast")
}

repositories {
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  implementation(project(":common"))
  implementation(project(":parser"))

  intellijPlatform {
    intellijIdeaCommunity("2024.2")
    pluginVerifier()
    zipSigner()
    instrumentationTools()
  }
}

porkAst {
  astCodegenType.set(AstCodegenType.PorkIdea)
}

project.afterEvaluate {
  tasks.buildPlugin {
    exclude("**/lib/annotations*.jar")
    exclude("**/lib/kotlin*.jar")
  }
}
