package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("stringLiteral")
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
