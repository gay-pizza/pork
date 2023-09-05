package gay.pizza.pork.gradle.codegen

class KotlinEnum(
  override val pkg: String,
  override val name: String,
  override var imports: MutableList<String> = mutableListOf(),
  override var inherits: MutableList<String> = mutableListOf(),
  override var annotations: MutableList<String> = mutableListOf(),
  override var members: MutableList<KotlinMember> = mutableListOf(),
  override var functions: MutableList<KotlinFunction> = mutableListOf(),
  var entries: MutableList<KotlinEnumEntry> = mutableListOf(),
) : KotlinClassLike()
