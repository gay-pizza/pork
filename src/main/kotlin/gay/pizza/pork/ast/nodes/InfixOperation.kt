package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class InfixOperation(
  val left: Expression,
  val op: InfixOperator,
  val right: Expression
) : Expression() {
  override val type: NodeType = NodeType.InfixOperation

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(left, right)

  override fun equals(other: Any?): Boolean {
    if (other !is InfixOperation) return false
    return other.op == op &&
      other.left == left &&
      other.right == right
  }

  override fun hashCode(): Int {
    var result = left.hashCode()
    result = 31 * result + op.hashCode()
    result = 31 * result + right.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
