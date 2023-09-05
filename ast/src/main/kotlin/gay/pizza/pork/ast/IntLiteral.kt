// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("intLiteral")
class IntLiteral(val value: Int) : Expression() {
  override val type: NodeType = NodeType.IntLiteral

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitIntLiteral(this)

  override fun equals(other: Any?): Boolean {
    if (other !is IntLiteral) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
