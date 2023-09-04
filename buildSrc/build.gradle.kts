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
      implementationClass = "gay.pizza.pork.gradle.PorkAstPlugin"

      displayName = "Pork AST"
      description = "AST generation code for pork"
    }
  }
}
