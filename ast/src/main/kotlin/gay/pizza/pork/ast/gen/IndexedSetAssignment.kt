// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("indexedSetAssignment")
class IndexedSetAssignment(val target: Expression, val index: Expression, val value: Expression) : Expression() {
  override val type: NodeType = NodeType.IndexedSetAssignment

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(target, index, value)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitIndexedSetAssignment(this)

  override fun equals(other: Any?): Boolean {
    if (other !is IndexedSetAssignment) return false
    return other.target == target && other.index == index && other.value == value
  }

  override fun hashCode(): Int {
    var result = target.hashCode()
    result = 31 * result + index.hashCode()
    result = 31 * result + value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
