package gay.pizza.pork.ast

class Program(val expressions: List<Expression>) : Node {
  override val type: NodeType = NodeType.Program

  override fun <T> visitChildren(visitor: Visitor<T>): List<T> =
    visitor.visitAll(expressions)
}
