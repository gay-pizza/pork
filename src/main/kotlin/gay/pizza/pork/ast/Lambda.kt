package gay.pizza.pork.ast

class Lambda(val expressions: List<Expression>) : Expression {
  constructor(vararg expressions: Expression) : this(listOf(*expressions))

  override val type: NodeType = NodeType.Lambda

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    expressions.map { expression ->  visitor.visit(expression) }
}
