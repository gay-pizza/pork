import gay.pizza.pork.buildext.AstCodegenType

plugins {
  id("org.jetbrains.intellij.platform") version "2.7.0"
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
    intellijIdeaCommunity("2024.3")
    pluginVerifier()
    zipSigner()
  }
}

intellijPlatform {
  pluginConfiguration {
    ideaVersion {
      sinceBuild = "243"
    }
  }

  buildSearchableOptions.set(false)
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
