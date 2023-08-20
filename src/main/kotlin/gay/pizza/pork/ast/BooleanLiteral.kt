package gay.pizza.pork.ast

class BooleanLiteral(val value: Boolean) : Expression() {
  override val type: NodeType = NodeType.BooleanLiteral
}
