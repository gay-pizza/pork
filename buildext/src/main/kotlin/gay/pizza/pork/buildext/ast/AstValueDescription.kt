package gay.pizza.pork.buildext.ast

data class AstValueDescription(
  val name: String,
  val type: String,
  val required: Boolean = false
)
