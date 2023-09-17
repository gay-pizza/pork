package gay.pizza.pork.buildext.codegen

class KotlinClass(
  override val pkg: String,
  override var name: String,
  var isSealed: Boolean = false,
  var isInterface: Boolean = false,
  var isOpen: Boolean = false,
  var isAbstract: Boolean = false,
  var isObject: Boolean = false,
  override var imports: MutableList<String> = mutableListOf(),
  override var annotations: MutableList<String> = mutableListOf(),
  override var typeParameters: MutableList<String> = mutableListOf(),
  override var inherits: MutableList<String> = mutableListOf(),
  override var members: MutableList<KotlinMember> = mutableListOf(),
  override var functions: MutableList<KotlinFunction> = mutableListOf(),
  override var constructorParameters: MutableMap<String, String> = mutableMapOf()
) : KotlinClassLike()
