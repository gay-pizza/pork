package gay.pizza.pork.ast

class InfixOperation(val left: Expression, val op: InfixOperator, val right: Expression) : Expression {
  override val type: NodeType = NodeType.InfixOperation

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    listOf(visitor.visit(left), visitor.visit(right))
}
