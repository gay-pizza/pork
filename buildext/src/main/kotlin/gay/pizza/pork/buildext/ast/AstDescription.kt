package gay.pizza.pork.buildext.ast

import kotlinx.serialization.Serializable

@Serializable
data class AstDescription(
  val root: String,
  val types: Map<String, AstTypeDescription>
)
