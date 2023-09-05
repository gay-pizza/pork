package gay.pizza.pork.gradle.codegen

class KotlinClass(
  override val pkg: String,
  override var name: String,
  var sealed: Boolean = false,
  override var inherits: MutableList<String> = mutableListOf(),
  override var imports: MutableList<String> = mutableListOf(),
  override var members: MutableList<KotlinMember> = mutableListOf(),
  override var annotations: MutableList<String> = mutableListOf(),
  override var functions: MutableList<KotlinFunction> = mutableListOf()
) : KotlinClassLike()
