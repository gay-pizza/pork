// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("booleanLiteral")
class BooleanLiteral(val value: Boolean) : Expression() {
  override val type: NodeType = NodeType.BooleanLiteral

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitBooleanLiteral(this)

  override fun equals(other: Any?): Boolean {
    if (other !is BooleanLiteral) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
