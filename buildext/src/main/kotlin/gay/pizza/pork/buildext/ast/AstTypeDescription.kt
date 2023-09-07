package gay.pizza.pork.buildext.ast

data class AstTypeDescription(
  val parent: String? = null,
  val values: List<AstValueDescription>? = null,
  val enums: List<AstEnumDescription> = emptyList()
)
