package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("define")
class Define(val symbol: Symbol, val value: Expression) : Expression() {
  override val type: NodeType = NodeType.Define

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol, value)

  override fun equals(other: Any?): Boolean {
    if (other !is Define) return false
    return other.symbol == symbol && other.value == value
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
