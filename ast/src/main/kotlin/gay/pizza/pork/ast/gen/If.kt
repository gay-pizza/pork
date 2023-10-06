// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("if")
class If(val condition: Expression, val thenBlock: Block, val elseBlock: Block?) : Expression() {
  override val type: NodeType = NodeType.If

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(condition, thenBlock, elseBlock)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitIf(this)

  override fun equals(other: Any?): Boolean {
    if (other !is If) return false
    return other.condition == condition && other.thenBlock == thenBlock && other.elseBlock == elseBlock
  }

  override fun hashCode(): Int {
    var result = condition.hashCode()
    result = 31 * result + thenBlock.hashCode()
    result = 31 * result + elseBlock.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
