package gay.pizza.pork.gradle.ast

data class AstValueDescription(
  val name: String,
  val type: String,
  val required: Boolean = false
)
