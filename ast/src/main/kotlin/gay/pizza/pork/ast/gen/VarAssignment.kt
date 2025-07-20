// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("varAssignment")
class VarAssignment(val symbol: Symbol, val value: Expression, val typeSpec: TypeSpec?) : Expression() {
  override val type: NodeType = NodeType.VarAssignment

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol, value, typeSpec)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitVarAssignment(this)

  override fun equals(other: Any?): Boolean {
    if (other !is VarAssignment) return false
    return other.symbol == symbol && other.value == value && other.typeSpec == typeSpec
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + value.hashCode()
    result = 31 * result + typeSpec.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
