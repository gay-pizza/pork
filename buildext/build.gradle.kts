@file:Suppress("UnstableApiUsage")
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
  implementation("org.jetbrains.kotlin:kotlin-serialization:2.1.10")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
  implementation("com.charleskorn.kaml:kaml:0.72.0")
}

java {
  val javaVersion = JavaVersion.toVersion(21)
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

tasks.withType<KotlinCompile> {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_21)
  }
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
