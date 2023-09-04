package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("prefixOperator")
enum class PrefixOperator(val token: String) {
  Negate("!")
}
