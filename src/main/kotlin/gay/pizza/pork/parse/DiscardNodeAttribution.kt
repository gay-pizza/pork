package gay.pizza.pork.parse

import gay.pizza.pork.ast.nodes.Node

object DiscardNodeAttribution : NodeAttribution {
  override fun enter() {}
  override fun push(token: Token) {}
  override fun <T : Node> exit(node: T): T = node
}
