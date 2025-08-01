// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("symbolSetAssignment")
class SymbolSetAssignment(val symbol: Symbol, val value: Expression) : Expression() {
  override val type: NodeType = NodeType.SymbolSetAssignment

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol, value)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitSymbolSetAssignment(this)

  override fun equals(other: Any?): Boolean {
    if (other !is SymbolSetAssignment) return false
    return other.symbol == symbol && other.value == value
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
