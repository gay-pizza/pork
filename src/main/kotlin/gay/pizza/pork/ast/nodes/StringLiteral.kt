package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType

class StringLiteral(val text: String) : Expression() {
  override val type: NodeType = NodeType.StringLiteral

  override fun equals(other: Any?): Boolean {
    if (other !is StringLiteral) return false
    return other.text == text
  }

  override fun hashCode(): Int {
    var result = text.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
