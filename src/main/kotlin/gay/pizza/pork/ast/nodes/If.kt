package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType

class If(
  val condition: Expression,
  val thenExpression: Expression,
  val elseExpression: Expression? = null
) : Expression() {
  override val type: NodeType = NodeType.If
}
