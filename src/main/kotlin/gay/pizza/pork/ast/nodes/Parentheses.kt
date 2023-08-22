package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("parentheses")
class Parentheses(val expression: Expression) : Expression() {
  override val type: NodeType = NodeType.Parentheses

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(expression)

  override fun equals(other: Any?): Boolean {
    if (other !is Parentheses) return false
    return other.expression == expression
  }

  override fun hashCode(): Int {
    var result = expression.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
