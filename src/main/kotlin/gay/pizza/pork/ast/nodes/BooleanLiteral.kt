package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType

class BooleanLiteral(val value: Boolean) : Expression() {
  override val type: NodeType = NodeType.BooleanLiteral

  override fun equals(other: Any?): Boolean {
    if (other !is BooleanLiteral) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
