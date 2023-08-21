package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class FunctionCall(val symbol: Symbol, val arguments: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.FunctionCall

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(listOf(symbol), arguments)
}
