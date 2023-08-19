package gay.pizza.pork.ast

class Define(val symbol: Symbol, val value: Expression) : Expression {
  override val type: NodeType = NodeType.Define

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    listOf(visitor.visit(symbol), visitor.visit(value))
}
