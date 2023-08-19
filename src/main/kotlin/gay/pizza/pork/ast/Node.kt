package gay.pizza.pork.ast

interface Node {
  val type: NodeType
  fun <T> visitChildren(visitor: Visitor<T>): List<T> = emptyList()
}
