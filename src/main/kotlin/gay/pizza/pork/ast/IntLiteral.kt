package gay.pizza.pork.ast

class IntLiteral(val value: Int) : Expression() {
  override val type: NodeType = NodeType.IntLiteral
}
