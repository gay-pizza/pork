// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("integerLiteral")
class IntegerLiteral(val value: Int) : Expression() {
  override val type: NodeType = NodeType.IntegerLiteral

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitIntegerLiteral(this)

  override fun equals(other: Any?): Boolean {
    if (other !is IntegerLiteral) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
