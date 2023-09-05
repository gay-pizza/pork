// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("lambda")
class Lambda(val arguments: List<Symbol>, val expressions: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.Lambda

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(arguments, expressions)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitLambda(this)

  override fun equals(other: Any?): Boolean {
    if (other !is Lambda) return false
    return other.arguments == arguments && other.expressions == expressions
  }

  override fun hashCode(): Int {
    var result = arguments.hashCode()
    result = 31 * result + expressions.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
