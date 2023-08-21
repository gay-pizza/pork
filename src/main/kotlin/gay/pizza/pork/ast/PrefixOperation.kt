package gay.pizza.pork.ast

class PrefixOperation(val op: PrefixOperator, val expression: Expression) : Expression() {
  override val type: NodeType = NodeType.PrefixOperation
}
