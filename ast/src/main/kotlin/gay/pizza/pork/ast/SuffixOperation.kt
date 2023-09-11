// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("suffixOperation")
class SuffixOperation(val op: SuffixOperator, val reference: SymbolReference) : Expression() {
  override val type: NodeType = NodeType.SuffixOperation

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(reference)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitSuffixOperation(this)

  override fun equals(other: Any?): Boolean {
    if (other !is SuffixOperation) return false
    return other.op == op && other.reference == reference
  }

  override fun hashCode(): Int {
    var result = op.hashCode()
    result = 31 * result + reference.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
