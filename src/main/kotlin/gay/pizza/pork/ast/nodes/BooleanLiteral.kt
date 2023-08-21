package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType

class BooleanLiteral(val value: Boolean) : Expression() {
  override val type: NodeType = NodeType.BooleanLiteral
}
