package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.compiler.KotlinCompiler
import gay.pizza.pork.frontend.FileFrontend

class GenerateKotlinCommand : CliktCommand(help = "Generate Kotlin Code", name = "generate-kotlin") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val frontend = FileFrontend(path)
    println(frontend.visit(KotlinCompiler()))
  }
}
