// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("break")
class Break : Expression() {
  override val type: NodeType = NodeType.Break

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitBreak(this)

  override fun equals(other: Any?): Boolean {
    if (other !is Break) return false
    return true
  }

  override fun hashCode(): Int =
    31 * type.hashCode()
}
