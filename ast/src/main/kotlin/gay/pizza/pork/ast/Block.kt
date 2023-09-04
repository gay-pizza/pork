package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("block")
class Block(val expressions: List<Expression>) : Node() {
  override val type: NodeType = NodeType.Block

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(expressions)

  override fun equals(other: Any?): Boolean {
    if (other !is Block) return false
    return other.expressions == expressions
  }

  override fun hashCode(): Int {
    var result = expressions.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
