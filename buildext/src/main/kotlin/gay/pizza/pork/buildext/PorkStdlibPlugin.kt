package gay.pizza.pork.buildext

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class PorkStdlibPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val generateStdlibManifest = createGenerateStdlibManifest(target)
    val processResources = target.tasks.getByName("processResources")
    processResources.dependsOn(generateStdlibManifest)
  }

  private fun createGenerateStdlibManifest(project: Project): TaskProvider<GenerateStdlibManifest> =
    project.tasks.register("generateStdlibManifest", GenerateStdlibManifest::class)
}
