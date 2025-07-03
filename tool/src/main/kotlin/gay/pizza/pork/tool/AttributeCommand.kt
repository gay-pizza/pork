package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.gen.Node
import gay.pizza.pork.ast.gen.NodeCoalescer
import gay.pizza.pork.ast.gen.visit
import gay.pizza.pork.common.IndentPrinter
import gay.pizza.pork.minimal.FileTool
import gay.pizza.pork.parser.ParserAttributes
import gay.pizza.pork.parser.ParserNodeAttribution

class AttributeCommand : CliktCommand("attribute") {
  val hierarchical by option("--hierarchical", help = "Print Hierarchical Output").flag(default = true)
  val path by argument("file")

  override fun help(context: Context): String = "Attribute AST"

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val attribution = ParserNodeAttribution()
    val compilationUnit = tool.parse(attribution)

    if (hierarchical) {
      val output = IndentPrinter()
      fun visit(node: Node) {
        output.emitIndentedLine("${node.type.name} ->")
        output.indented {
          for (token in ParserAttributes.recallOwnedTokens(node)) {
            output.emitIndentedLine(token.toString())
          }
          node.visitChildren(NodeCoalescer(followChildren = false, handler = ::visit))
        }
      }
      visit(compilationUnit)
    } else {
      val coalescer = NodeCoalescer { node ->
        val allTokens = ParserAttributes.recallAllTokens(node)
        println("node ${node.type.name}")
        for (token in allTokens) {
          println("token $token")
        }
      }
      coalescer.visit(compilationUnit)
    }
  }
}
