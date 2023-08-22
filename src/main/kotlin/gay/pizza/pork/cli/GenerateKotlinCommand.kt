package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.compiler.KotlinCompiler
import gay.pizza.pork.parse.PorkParser
import gay.pizza.pork.parse.PorkTokenizer
import gay.pizza.pork.parse.StringCharSource
import gay.pizza.pork.parse.TokenStreamSource
import kotlin.io.path.readText

class GenerateKotlinCommand : CliktCommand(help = "Generate Kotlin Code", name = "generate-kotlin") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val content = path.readText()
    val tokenStream = PorkTokenizer(StringCharSource(content)).tokenize()
    val program = PorkParser(TokenStreamSource(tokenStream)).readProgram()
    val compiler = KotlinCompiler()
    println(compiler.visit(program))
  }
}
