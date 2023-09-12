// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("longLiteral")
class LongLiteral(val value: Long) : Expression() {
  override val type: NodeType = NodeType.LongLiteral

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitLongLiteral(this)

  override fun equals(other: Any?): Boolean {
    if (other !is LongLiteral) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
