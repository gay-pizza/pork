package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("intLiteral")
class IntLiteral(val value: Int) : Expression() {
  override val type: NodeType = NodeType.IntLiteral

  override fun equals(other: Any?): Boolean {
    if (other !is IntLiteral) return false
    return other.value == value
  }

  override fun hashCode(): Int {
    var result = value
    result = 31 * result + type.hashCode()
    return result
  }
}
