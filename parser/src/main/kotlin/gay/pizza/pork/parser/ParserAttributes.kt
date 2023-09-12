package gay.pizza.pork.parser

import gay.pizza.pork.ast.Node
import gay.pizza.pork.ast.NodeCoalescer
import gay.pizza.pork.ast.data
import gay.pizza.pork.ast.visit

data class ParserAttributes(val tokens: List<Token>) {
  companion object {
    fun recallAllTokens(node: Node): List<Token> {
      val all = mutableListOf<Token>()
      val coalescer = NodeCoalescer { item ->
        val attributes = item.data<ParserAttributes>()
        if (attributes != null) {
          all.addAll(attributes.tokens)
        }
      }
      coalescer.visit(node)
      all.sortBy { it.start }
      return all
    }
  }
}
