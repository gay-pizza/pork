package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class SymbolReference(val symbol: Symbol) : Expression() {
  override val type: NodeType = NodeType.SymbolReference

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol)
}
