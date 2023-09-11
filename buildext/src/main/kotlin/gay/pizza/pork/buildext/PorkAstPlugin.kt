package gay.pizza.pork.buildext

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class PorkAstPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val generateAstCode = createGenerateAstCode(target)
    val processResources = target.tasks.getByName("processResources")
    processResources.dependsOn(generateAstCode)
  }

  private fun createGenerateAstCode(project: Project): GenerateAstCode =
    project.tasks.create("generateAstCode", GenerateAstCode::class)
}
