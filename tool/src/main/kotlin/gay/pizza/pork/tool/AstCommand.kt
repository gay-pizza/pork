package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.Node
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class AstCommand : CliktCommand(help = "Print AST", name = "ast") {
  val path by argument("file")

  private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    classDiscriminator = "\$"
  }

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    println(json.encodeToString(Node.serializer(), tool.parse()))
  }
}
