package gay.pizza.pork.buildext.ast

class AstValue(
  val name: String,
  val typeRef: AstTypeRef,
  val abstract: Boolean = false,
  val defaultValue: String? = null
)
