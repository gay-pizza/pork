package gay.pizza.pork.buildext

import gay.pizza.pork.buildext.ast.AstCodegen
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory
import java.io.File

open class GenerateAstCode : DefaultTask() {
  init {
    outputs.upToDateWhen { false }
  }

  @get:InputFile
  var astDescriptionFile: File = project.file("src/main/ast/pork.yml")

  @get:Input
  var codePackage: String = "gay.pizza.pork.ast"

  @get:OutputDirectory
  var outputDirectory: File = project.file("src/main/kotlin/gay/pizza/pork/ast")

  @TaskAction
  fun generate() {
    AstCodegen.run(codePackage, astDescriptionFile.toPath(), outputDirectory.toPath())
  }
}
