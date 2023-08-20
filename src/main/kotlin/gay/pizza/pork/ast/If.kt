package gay.pizza.pork.ast

class If(
  val condition: Expression,
  val thenExpression: Expression,
  val elseExpression: Expression
) : Expression {
  override val type: NodeType = NodeType.If
}
