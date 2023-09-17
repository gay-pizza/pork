package gay.pizza.pork.buildext

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType

class PorkAstPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.extensions.create("porkAst", PorkAstExtension::class)

    target.afterEvaluate {
      val generateAstCode = createGenerateAstCode(target)
      val processResources = target.tasks.getByName("processResources")
      processResources.dependsOn(generateAstCode)
      val compileKotlin = target.tasks.getByName("compileKotlin")
      compileKotlin.dependsOn(generateAstCode)
    }
  }

  private fun getAstExtension(project: Project): PorkAstExtension =
    project.extensions.getByType<PorkAstExtension>()

  private fun createGenerateAstCode(project: Project): Task {
    val extension = getAstExtension(project)
    val codegenType = extension.astCodegenType.get()
    if (codegenType == AstCodegenType.Standard) {
      return project.tasks.create("generateAstCode", GenerateStandardAstCode::class)
    }
    return project.tasks.create("generateAstCode", GeneratePorkIdeaAstCode::class)
  }
}
