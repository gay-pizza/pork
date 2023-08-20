package gay.pizza.pork.ast

class Lambda(val arguments: List<Symbol>, val expressions: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.Lambda

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    visitor.visitAll(arguments, expressions)
}
