package gay.pizza.pork.buildext.ast

import kotlinx.serialization.Serializable

@Serializable
class AstEnumDescription(
  val name: String,
  val values: Map<String, String>
)
