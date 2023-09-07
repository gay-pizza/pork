// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("while")
class While(val condition: Expression, val block: Block) : Expression() {
  override val type: NodeType = NodeType.While

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(condition, block)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitWhile(this)

  override fun equals(other: Any?): Boolean {
    if (other !is While) return false
    return other.condition == condition && other.block == block
  }

  override fun hashCode(): Int {
    var result = condition.hashCode()
    result = 31 * result + block.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
