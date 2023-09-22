package gay.pizza.pork.buildext.ast

import kotlinx.serialization.Serializable

@Serializable
data class AstTypeDescription(
  val parent: String? = null,
  val values: List<AstValueDescription>? = null,
  val enums: List<AstEnumDescription> = emptyList(),
  val namedElementValue: String? = null,
  val referencedElementValue: String? = null,
  val referencedElementType: String? = null
)
