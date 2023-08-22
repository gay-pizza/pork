package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("symbol")
class Symbol(val id: String) : Node() {
  override val type: NodeType = NodeType.Symbol

  override fun equals(other: Any?): Boolean {
    if (other !is Symbol) return false
    return other.id == id
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
