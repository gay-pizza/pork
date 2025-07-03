package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.gen.Node
import gay.pizza.pork.minimal.FileTool
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class AstCommand : CliktCommand("ast") {
  val path by argument("file")

  override fun help(context: Context): String = "Print AST"

  private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    classDiscriminator = "$"
  }

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    println(json.encodeToString(Node.serializer(), tool.parse()))
  }
}
