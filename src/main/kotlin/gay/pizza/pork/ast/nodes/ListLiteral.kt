package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class ListLiteral(val items: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.ListLiteral

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(items)

  override fun equals(other: Any?): Boolean {
    if (other !is ListLiteral) return false
    return other.items == items
  }

  override fun hashCode(): Int {
    var result = items.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
