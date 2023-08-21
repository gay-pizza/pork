package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class ListLiteral(val items: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.ListLiteral

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(items)
}
