package gay.pizza.pork.buildext

import gay.pizza.pork.buildext.ast.AstPorkIdeaCodegen
import gay.pizza.pork.buildext.ast.AstWorld
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GeneratePorkIdeaAstCode : DefaultTask() {
  @get:InputFile
  var astDescriptionFile: File = project.project(":ast").file("src/main/ast/pork.yml")

  @get:Input
  var codePackage: String = "gay.pizza.pork.idea.psi.gen"

  @get:OutputDirectory
  var outputDirectory: File = project.file("src/main/kotlin/gay/pizza/pork/idea/psi/gen")

  @TaskAction
  fun generate() {
    val world = AstWorld.read(astDescriptionFile.toPath())
    AstPorkIdeaCodegen.run(codePackage, world, outputDirectory.toPath())
  }
}
