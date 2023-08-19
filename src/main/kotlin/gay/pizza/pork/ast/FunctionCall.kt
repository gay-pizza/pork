package gay.pizza.pork.ast

class FunctionCall(val symbol: Symbol) : Expression {
  override val type: NodeType = NodeType.FunctionCall

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    listOf(visitor.visit(symbol))
}
