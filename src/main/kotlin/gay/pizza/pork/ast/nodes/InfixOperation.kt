package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor

class InfixOperation(val left: Expression, val op: InfixOperator, val right: Expression) : Expression() {
  override val type: NodeType = NodeType.InfixOperation

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(left, right)
}
