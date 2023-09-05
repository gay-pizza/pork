// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("parentheses")
class Parentheses(val expression: Expression) : Expression() {
  override val type: NodeType = NodeType.Parentheses

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(expression)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitParentheses(this)

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
