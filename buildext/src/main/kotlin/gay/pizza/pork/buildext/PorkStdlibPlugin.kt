package gay.pizza.pork.buildext

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class PorkStdlibPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val generateStdlibManifest = createGenerateStdlibManifest(target)
    val processResources = target.tasks.getByName("processResources")
    processResources.dependsOn(generateStdlibManifest)
  }

  private fun createGenerateStdlibManifest(project: Project): GenerateStdlibManifest =
    project.tasks.create("generateStdlibManifest", GenerateStdlibManifest::class)
}
