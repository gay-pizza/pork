package gay.pizza.pork.buildext.codegen

class KotlinFunction(
  val name: String,
  var annotations: MutableList<String> = mutableListOf(),
  var typeParameters: MutableList<String> = mutableListOf(),
  var extensionOf: String? = null,
  var parameters: MutableList<KotlinParameter> = mutableListOf(),
  var returnType: String? = null,
  var abstract: Boolean = false,
  var open: Boolean = false,
  var inline: Boolean = false,
  var overridden: Boolean = false,
  var isImmediateExpression: Boolean = false,
  var body: MutableList<String> = mutableListOf(),
  var isInterfaceMethod: Boolean = false
)
