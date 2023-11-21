// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("return")
class Return(val value: Expression) : Expression() {
  override val type: NodeType = NodeType.Return

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(value)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitReturn(this)

  override fun equals(other: Any?): Boolean {
    if (other !is Return) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
