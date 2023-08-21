package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.Printer
import gay.pizza.pork.ast.NodeVisitor

abstract class Node {
  abstract val type: NodeType
  open fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> = emptyList()

  override fun toString(): String = let { node -> buildString { Printer(this).visit(node) } }
}
