package gay.pizza.pork.parser

import gay.pizza.pork.ast.Node
import gay.pizza.pork.ast.data

class ParserNodeAttribution : NodeAttribution {
  private val stack = mutableListOf<MutableList<Token>>()
  private var current: MutableList<Token>? = null

  override fun enter() {
    val store = mutableListOf<Token>()
    current = store
    stack.add(store)
  }

  override fun push(token: Token) {
    val store = current ?: throw RuntimeException("enter() not called!")
    store.add(token)
  }

  override fun <T : Node> adopt(node: T) {
    val attributes = node.data<ParserAttributes>()
    if (attributes != null) {
      for (token in attributes.tokens) {
        push(token)
      }
      node.data = ParserAttributes(emptyList())
    }
  }

  override fun <T: Node> exit(node: T): T {
    val store = stack.removeLast()
    current = stack.lastOrNull()
    node.data = ParserAttributes(store)
    return node
  }
}
