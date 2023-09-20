package gay.pizza.pork.buildext.ast

import kotlinx.serialization.Serializable

@Serializable
data class AstValueDescription(
  val name: String,
  val type: String,
  val required: Boolean = false,
  val defaultValue: String? = null
)
