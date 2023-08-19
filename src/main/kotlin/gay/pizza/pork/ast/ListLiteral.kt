package gay.pizza.pork.ast

class ListLiteral(val items: List<Expression>) : Expression {
  constructor(vararg items: Expression) : this(listOf(*items))

  override val type: NodeType = NodeType.ListLiteral

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    items.map { visitor.visit(it) }
}
