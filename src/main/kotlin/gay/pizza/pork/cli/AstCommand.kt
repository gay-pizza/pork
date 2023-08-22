package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.ast.nodes.Node
import gay.pizza.pork.parse.PorkParser
import gay.pizza.pork.parse.PorkTokenizer
import gay.pizza.pork.parse.StringCharSource
import gay.pizza.pork.parse.TokenStreamSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.io.path.readText

@OptIn(ExperimentalSerializationApi::class)
class AstCommand : CliktCommand(help = "Print AST", name = "ast") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    classDiscriminator = "\$"
  }

  override fun run() {
    val content = path.readText()
    val tokenStream = PorkTokenizer(StringCharSource(content)).tokenize()
    val program = PorkParser(TokenStreamSource(tokenStream)).readProgram()
    println(json.encodeToString(Node.serializer(), program))
  }
}
