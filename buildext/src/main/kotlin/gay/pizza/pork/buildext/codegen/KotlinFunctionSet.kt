package gay.pizza.pork.buildext.codegen

class KotlinFunctionSet(
  val pkg: String,
  var imports: MutableList<String> = mutableListOf(),
  var functions: MutableList<KotlinFunction> = mutableListOf()
)
