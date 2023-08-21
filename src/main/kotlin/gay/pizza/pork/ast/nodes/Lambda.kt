package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class Lambda(val arguments: List<Symbol>, val expressions: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.Lambda

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(arguments, expressions)

  override fun equals(other: Any?): Boolean {
    if (other !is Lambda) return false
    return other.arguments == arguments && other.expressions == expressions
  }

  override fun hashCode(): Int {
    var result = arguments.hashCode()
    result = 31 * result + expressions.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
