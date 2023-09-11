package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.NodeCoalescer
import gay.pizza.pork.ast.visit
import gay.pizza.pork.minimal.FileTool
import gay.pizza.pork.parser.TokenNodeAttribution

class AttributeCommand : CliktCommand(help = "Attribute AST", name = "attribute") {
  val path by argument("file")

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val attribution = TokenNodeAttribution()
    val compilationUnit = tool.parse(attribution)

    val coalescer = NodeCoalescer { node ->
      val tokens = attribution.assembleTokens(node)
      println("node ${node.type.name}")
      for (token in tokens) {
        println("token $token")
      }
    }
    coalescer.visit(compilationUnit)
  }
}
