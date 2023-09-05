package gay.pizza.pork.buildext

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

open class PorkModulePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.apply(plugin = "org.jetbrains.kotlin.jvm")
    target.apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    target.repositories.mavenCentral()

    target.extensions.getByType<JavaPluginExtension>().apply {
      val javaVersion = JavaVersion.toVersion(17)
      sourceCompatibility = javaVersion
      targetCompatibility = javaVersion
    }

    target.tasks.withType<KotlinCompile> {
      kotlinOptions.jvmTarget = "17"
    }

    target.dependencies {
      add("implementation", "org.jetbrains.kotlin:kotlin-bom")
      add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    }
  }
}
