package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("functionDeclaration")
class FunctionDeclaration(val symbol: Symbol, val arguments: List<Symbol>, val block: Block) : Declaration() {
  override val type: NodeType = NodeType.FunctionDeclaration

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol)

  override fun equals(other: Any?): Boolean {
    if (other !is FunctionDeclaration) return false
    return other.symbol == symbol && other.arguments == arguments && other.block == block
  }

  override fun hashCode(): Int {
    var result = symbol.hashCode()
    result = 31 * result + symbol.hashCode()
    result = 31 * result + arguments.hashCode()
    result = 31 * result + block.hashCode()
    return result
  }
}
