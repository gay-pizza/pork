package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.ast.nodes.Node
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class AstCommand : CliktCommand(help = "Print AST", name = "ast") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    classDiscriminator = "\$"
  }

  override fun run() {
    val tool = FileTool(path)
    println(json.encodeToString(Node.serializer(), tool.parse()))
  }
}
