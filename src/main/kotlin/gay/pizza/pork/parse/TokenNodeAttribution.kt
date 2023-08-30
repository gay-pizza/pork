package gay.pizza.pork.parse

import gay.pizza.pork.ast.NodeCoalescer
import gay.pizza.pork.ast.nodes.Node
import java.util.IdentityHashMap

class TokenNodeAttribution : NodeAttribution {
  private val map: MutableMap<Node, List<Token>> = IdentityHashMap()

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

  override fun <T: Node> exit(node: T): T {
    val store = stack.removeLast()
    map[node] = store
    return node
  }

  fun tokensOf(node: Node): List<Token>? = map[node]

  fun assembleTokens(node: Node): List<Token> {
    val allTokens = mutableListOf<Token>()
    val coalescer = NodeCoalescer { item ->
      val tokens = tokensOf(item)
      if (tokens != null) {
        allTokens.addAll(tokens)
      }
    }
    coalescer.visit(node)
    return allTokens
  }
}
