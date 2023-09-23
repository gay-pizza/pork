package gay.pizza.pork.buildext

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

open class PorkModulePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.apply(plugin = "org.jetbrains.kotlin.jvm")
    target.apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    target.repositories.mavenCentral()
    target.repositories.maven(url = "https://gitlab.com/api/v4/projects/49101454/packages/maven")

    target.extensions.getByType<JavaPluginExtension>().apply {
      val javaVersion = JavaVersion.toVersion(21)
      sourceCompatibility = javaVersion
      targetCompatibility = javaVersion
    }

    target.tasks.withType<KotlinCompile> {
      kotlinOptions.jvmTarget = "21"
    }

    target.extensions.getByType<KotlinJvmProjectExtension>().apply {
      jvmToolchain(21)
    }

    target.dependencies {
      add("implementation", "org.jetbrains.kotlin:kotlin-bom")
      add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
      add("api", "gay.pizza.dough:dough-core:0.1.0-SNAPSHOT")
      add("api", "gay.pizza.dough:dough-fs:0.1.0-SNAPSHOT")
    }
  }
}
