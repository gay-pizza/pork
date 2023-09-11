package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.minimal.FileTool

class TokenizeCommand : CliktCommand(help = "Tokenize Compilation Unit", name = "tokenize") {
  val path by argument("file")

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val tokenStream = tool.tokenize()
    for (token in tokenStream.tokens) {
      println("${token.start} ${token.type.name} '${sanitize(token.text)}'")
    }
  }

  private fun sanitize(input: String): String =
    input
      .replace("\n", "\\n")
      .replace("\r", "\\r")
}
