package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("symbolReference")
class SymbolReference(val symbol: Symbol) : Expression() {
  override val type: NodeType = NodeType.SymbolReference

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol)

  override fun equals(other: Any?): Boolean {
    if (other !is SymbolReference) return false
    return other.symbol == symbol
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
