package gay.pizza.pork.ast

class ListLiteral(val items: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.ListLiteral

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    visitor.visitAll(items)
}
