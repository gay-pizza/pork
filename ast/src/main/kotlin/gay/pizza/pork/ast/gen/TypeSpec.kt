// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("typeSpec")
class TypeSpec(val symbol: Symbol) : Node() {
  override val type: NodeType = NodeType.TypeSpec

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitTypeSpec(this)

  override fun equals(other: Any?): Boolean {
    if (other !is TypeSpec) return false
    return other.symbol == symbol
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
