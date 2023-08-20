package gay.pizza.pork.ast

class Symbol(val id: String) : Node() {
  override val type: NodeType = NodeType.Symbol
}
