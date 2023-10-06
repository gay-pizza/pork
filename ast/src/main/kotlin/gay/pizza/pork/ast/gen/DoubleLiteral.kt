// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("doubleLiteral")
class DoubleLiteral(val value: Double) : Expression() {
  override val type: NodeType = NodeType.DoubleLiteral

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitDoubleLiteral(this)

  override fun equals(other: Any?): Boolean {
    if (other !is DoubleLiteral) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
