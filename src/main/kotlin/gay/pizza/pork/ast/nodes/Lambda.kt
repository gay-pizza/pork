package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class Lambda(val arguments: List<Symbol>, val expressions: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.Lambda

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(arguments, expressions)
}
