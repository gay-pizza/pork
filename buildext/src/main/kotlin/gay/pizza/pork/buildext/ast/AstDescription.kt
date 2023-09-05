package gay.pizza.pork.buildext.ast

data class AstDescription(
  val root: String,
  val types: Map<String, AstTypeDescription>
)
