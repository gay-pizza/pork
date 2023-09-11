plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
  implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.10")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
}

gradlePlugin {
  plugins {
    create("pork_ast") {
      id = "gay.pizza.pork.ast"
      implementationClass = "gay.pizza.pork.buildext.PorkAstPlugin"

      displayName = "Pork AST"
      description = "AST generation code for pork"
    }

    create("pork_module") {
      id = "gay.pizza.pork.module"
      implementationClass = "gay.pizza.pork.buildext.PorkModulePlugin"

      displayName = "Pork Module"
      description = "Module convention for pork"
    }

    create("pork_stdlib") {
      id = "gay.pizza.pork.stdlib"
      implementationClass = "gay.pizza.pork.buildext.PorkStdlibPlugin"

      displayName = "Pork Stdlib"
      description = "Stdlib generation code for pork"
    }
  }
}
