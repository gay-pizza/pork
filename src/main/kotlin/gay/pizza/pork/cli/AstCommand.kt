package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.ast.nodes.Node
import gay.pizza.pork.frontend.FileFrontend
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
    val frontend = FileFrontend(path)
    println(json.encodeToString(Node.serializer(), frontend.parse()))
  }
}
