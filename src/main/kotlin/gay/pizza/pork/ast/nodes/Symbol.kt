package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType

class Symbol(val id: String) : Node() {
  override val type: NodeType = NodeType.Symbol
}
