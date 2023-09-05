package gay.pizza.pork.buildext.codegen

class KotlinParameter(
  val name: String,
  var type: String,
  var defaultValue: String? = null,
  var vararg: Boolean = false
)
