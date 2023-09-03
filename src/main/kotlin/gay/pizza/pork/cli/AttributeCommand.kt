package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.ast.NodeCoalescer
import gay.pizza.pork.parse.TokenNodeAttribution

class AttributeCommand : CliktCommand(help = "Attribute AST", name = "attribute") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val tool = FileTool(path)
    val attribution = TokenNodeAttribution()
    val compilationUnit = tool.parse(attribution)

    val coalescer = NodeCoalescer { node ->
      val tokens = attribution.assembleTokens(node)
      println("node ${node.toString().replace("\n", "^")}")
      for (token in tokens) {
        println("token $token")
      }
    }
    coalescer.visit(compilationUnit)
  }
}
