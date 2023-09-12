package gay.pizza.pork.parser

import gay.pizza.pork.ast.Node

object DiscardNodeAttribution : NodeAttribution {
  override fun push(token: Token) {}
  override fun <T : Node> adopt(node: T) {}
  override fun <T : Node> guarded(block: () -> T): T =
    block()
}
