@file:Suppress("UnstableApiUsage")
plugins {
  `kotlin-dsl`
  embeddedKotlin("plugin.serialization")
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
  implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.10")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("com.charleskorn.kaml:kaml:0.55.0")
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
