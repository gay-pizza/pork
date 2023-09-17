package gay.pizza.pork.buildext

import gay.pizza.pork.buildext.ast.AstStandardCodegen
import gay.pizza.pork.buildext.ast.AstGraph
import gay.pizza.pork.buildext.ast.AstWorld
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import java.io.File
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

open class GenerateStandardAstCode : DefaultTask() {
  @get:InputFile
  var astDescriptionFile: File = project.file("src/main/ast/pork.yml")

  @get:Input
  var codePackage: String = "gay.pizza.pork.ast"

  @get:OutputDirectory
  var outputDirectory: File = project.file("src/main/kotlin/gay/pizza/pork/ast")

  @get:OutputFile
  var typeGraphFile: File = project.file("src/main/graph/types.dot")

  @TaskAction
  fun generate() {
    val world = AstWorld.read(astDescriptionFile.toPath())
    AstStandardCodegen.run(codePackage, world, outputDirectory.toPath())

    val typeGraphPath = typeGraphFile.toPath()
    typeGraphPath.deleteIfExists()
    typeGraphPath.parent.createDirectories()
    val graph = AstGraph.from(world)
    typeGraphPath.writeText(graph.renderDotGraph())
  }
}
