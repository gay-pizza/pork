package gay.pizza.pork.ast

abstract class Node {
  abstract val type: NodeType
  open fun <T> visitChildren(visitor: Visitor<T>): List<T> = emptyList()

  override fun toString(): String = let { node -> buildString { Printer(this).visit(node) } }
}
