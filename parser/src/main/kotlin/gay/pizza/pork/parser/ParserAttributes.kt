package gay.pizza.pork.parser

import gay.pizza.pork.ast.gen.Node
import gay.pizza.pork.ast.gen.NodeCoalescer
import gay.pizza.pork.ast.gen.data
import gay.pizza.pork.ast.gen.visit

data class ParserAttributes(val tokens: List<Token>) {
  companion object {
    fun recallOwnedTokens(node: Node): List<Token> {
      val attributes = node.data<ParserAttributes>()
      if (attributes != null) {
        return attributes.tokens
      }
      return emptyList()
    }

    fun recallAllTokens(node: Node): List<Token> {
      val all = mutableListOf<Token>()
      val coalescer = NodeCoalescer { item ->
        val attributes = item.data<ParserAttributes>()
        if (attributes != null) {
          all.addAll(attributes.tokens)
        }
      }
      coalescer.visit(node)
      all.sortBy { it.sourceIndex.index }
      return all
    }
  }
}
