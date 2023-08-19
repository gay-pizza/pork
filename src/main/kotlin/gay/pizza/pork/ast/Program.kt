package gay.pizza.pork.ast

class Program(val expressions: List<Expression>) : Node {
  constructor(vararg expressions: Expression) : this(listOf(*expressions))

  override val type: NodeType = NodeType.Program

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    expressions.map { visitor.visit(it) }
}
