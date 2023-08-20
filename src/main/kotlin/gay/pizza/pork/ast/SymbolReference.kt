package gay.pizza.pork.ast

class SymbolReference(val symbol: Symbol) : Expression() {
  override val type: NodeType = NodeType.SymbolReference

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    visitor.visitNodes(symbol)
}
