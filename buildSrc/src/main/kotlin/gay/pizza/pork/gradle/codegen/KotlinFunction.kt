package gay.pizza.pork.gradle.codegen

class KotlinFunction(
  val name: String,
  var parameters: MutableList<KotlinParameter> = mutableListOf(),
  var returnType: String? = null,
  var abstract: Boolean = false,
  var overridden: Boolean = false,
  var isImmediateExpression: Boolean = false,
  var body: MutableList<String> = mutableListOf()
)
