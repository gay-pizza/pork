package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("prefixOperation")
class PrefixOperation(val op: PrefixOperator, val expression: Expression) : Expression() {
  override val type: NodeType = NodeType.PrefixOperation

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(expression)

  override fun equals(other: Any?): Boolean {
    if (other !is PrefixOperation) return false
    return other.op == op && other.expression == expression
  }

  override fun hashCode(): Int {
    var result = op.hashCode()
    result = 31 * result + expression.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
