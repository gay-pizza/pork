package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("functionCall")
class FunctionCall(val symbol: Symbol, val arguments: List<Expression>) : Expression() {
  override val type: NodeType = NodeType.FunctionCall

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(listOf(symbol), arguments)

  override fun equals(other: Any?): Boolean {
    if (other !is FunctionCall) return false
    return other.symbol == symbol && other.arguments == arguments
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + arguments.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
