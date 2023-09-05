package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("functionDefinition")
class FunctionDefinition(override val modifiers: DefinitionModifiers, override val symbol: Symbol, val arguments: List<Symbol>, val block: Block) : Definition() {
  override val type: NodeType = NodeType.FunctionDefinition

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(listOf(symbol), arguments, listOf(block))

  override fun equals(other: Any?): Boolean {
    if (other !is FunctionDefinition) return false
    return other.modifiers == modifiers && other.symbol == symbol && other.arguments == arguments && other.block == block
  }

  override fun hashCode(): Int {
    var result = modifiers.hashCode()
    result = 31 * result + symbol.hashCode()
    result = 31 * result + arguments.hashCode()
    result = 31 * result + block.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
