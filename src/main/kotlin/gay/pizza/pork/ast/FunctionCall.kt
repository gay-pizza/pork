package gay.pizza.pork.ast

class FunctionCall(val symbol: Symbol, val arguments: List<Expression>) : Expression {
  override val type: NodeType = NodeType.FunctionCall

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    visitor.visitAll(listOf(symbol), arguments)
}
