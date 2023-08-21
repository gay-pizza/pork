package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType

class IntLiteral(val value: Int) : Expression() {
  override val type: NodeType = NodeType.IntLiteral
}
