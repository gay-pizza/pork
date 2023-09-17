// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("indexedBy")
class IndexedBy(val expression: Expression, val index: Expression) : Expression() {
  override val type: NodeType = NodeType.IndexedBy

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(expression, index)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitIndexedBy(this)

  override fun equals(other: Any?): Boolean {
    if (other !is IndexedBy) return false
    return other.expression == expression && other.index == index
  }

  override fun hashCode(): Int {
    var result = expression.hashCode()
    result = 31 * result + index.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
