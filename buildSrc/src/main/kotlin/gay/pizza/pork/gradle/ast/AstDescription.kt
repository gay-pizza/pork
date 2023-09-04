package gay.pizza.pork.gradle.ast

data class AstDescription(
  val root: String,
  val types: Map<String, AstTypeDescription>
)
