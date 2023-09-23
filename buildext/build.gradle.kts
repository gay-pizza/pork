import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
  embeddedKotlin("plugin.serialization")
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20-Beta2")
  implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.20-Beta2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("com.charleskorn.kaml:kaml:0.55.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_20
  targetCompatibility = JavaVersion.VERSION_20
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "20"
}

gradlePlugin {
  plugins {
    create("pork_root") {
      id = "gay.pizza.pork.root"
      implementationClass = "gay.pizza.pork.buildext.PorkRootPlugin"

      displayName = "Pork Root"
      description = "Root convention for pork"
    }

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
