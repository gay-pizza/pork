package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("program")
class Program(val expressions: List<Expression>) : Node() {
  override val type: NodeType = NodeType.Program

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(expressions)

  override fun equals(other: Any?): Boolean {
    if (other !is Program) return false
    return other.expressions == expressions
  }

  override fun hashCode(): Int {
    var result = expressions.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
