package gay.pizza.pork.ast

class Parentheses(val expression: Expression) : Expression {
  override val type: NodeType = NodeType.Parentheses

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    listOf(visitor.visit(expression))
}
