package gay.pizza.pork.ast.nodes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("prefixOperator")
enum class PrefixOperator(val token: String) {
  Negate("!")
}
