// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("infixOperation")
class InfixOperation(val left: Expression, val op: InfixOperator, val right: Expression) : Expression() {
  override val type: NodeType = NodeType.InfixOperation

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(left, right)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitInfixOperation(this)

  override fun equals(other: Any?): Boolean {
    if (other !is InfixOperation) return false
    return other.left == left && other.op == op && other.right == right
  }

  override fun hashCode(): Int {
    var result = left.hashCode()
    result = 31 * result + op.hashCode()
    result = 31 * result + right.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
