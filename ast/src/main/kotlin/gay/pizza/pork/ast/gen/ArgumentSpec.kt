// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("argumentSpec")
class ArgumentSpec(val symbol: Symbol, val multiple: Boolean = false) : Node() {
  override val type: NodeType = NodeType.ArgumentSpec

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitArgumentSpec(this)

  override fun equals(other: Any?): Boolean {
    if (other !is ArgumentSpec) return false
    return other.symbol == symbol && other.multiple == multiple
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + multiple.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
