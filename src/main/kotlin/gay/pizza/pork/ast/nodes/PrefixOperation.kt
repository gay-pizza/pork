package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType

class PrefixOperation(val op: PrefixOperator, val expression: Expression) : Expression() {
  override val type: NodeType = NodeType.PrefixOperation
}
