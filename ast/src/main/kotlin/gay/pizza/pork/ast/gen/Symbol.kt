// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("symbol")
class Symbol(val id: String) : Node() {
  override val type: NodeType = NodeType.Symbol

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitSymbol(this)

  override fun equals(other: Any?): Boolean {
    if (other !is Symbol) return false
    return other.id == id
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
