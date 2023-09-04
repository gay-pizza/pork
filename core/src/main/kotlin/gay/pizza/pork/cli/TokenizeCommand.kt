package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path

class TokenizeCommand : CliktCommand(help = "Tokenize Compilation Unit", name = "tokenize") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val tool = FileTool(path)
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
