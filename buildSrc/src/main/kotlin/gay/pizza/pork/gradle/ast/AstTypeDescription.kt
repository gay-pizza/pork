package gay.pizza.pork.gradle.ast

data class AstTypeDescription(
  val parent: String? = null,
  val values: List<AstValueDescription> = emptyList(),
  val enums: List<AstEnumDescription> = emptyList()
)
