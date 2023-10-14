package gay.pizza.pork.parser

import gay.pizza.pork.ast.gen.Node
import gay.pizza.pork.ast.gen.NodeType
import gay.pizza.pork.ast.gen.data

open class ParserNodeAttribution : NodeAttribution {
  private val stack = mutableListOf<MutableList<Token>>()
  private var current: MutableList<Token>? = null

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

  override fun <T : Node> produce(type: NodeType, block: () -> T): T {
    var store = mutableListOf<Token>()
    current = store
    stack.add(store)
    val node = block()
    store = stack.removeAt(stack.size - 1)
    current = stack.lastOrNull()
    node.data = ParserAttributes(store)
    return node
  }
}
