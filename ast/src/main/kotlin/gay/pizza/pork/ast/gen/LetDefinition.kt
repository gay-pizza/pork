// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("letDefinition")
class LetDefinition(override val modifiers: DefinitionModifiers, override val symbol: Symbol, val value: Expression) : Definition() {
  override val type: NodeType = NodeType.LetDefinition

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol, value)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitLetDefinition(this)

  override fun equals(other: Any?): Boolean {
    if (other !is LetDefinition) return false
    return other.modifiers == modifiers && other.symbol == symbol && other.value == value
  }

  override fun hashCode(): Int {
    var result = modifiers.hashCode()
    result = 31 * result + symbol.hashCode()
    result = 31 * result + value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
