package gay.pizza.pork.buildext.codegen

abstract class KotlinClassLike {
  abstract val pkg: String
  abstract val name: String
  abstract var imports: MutableList<String>
  abstract var annotations: MutableList<String>
  abstract var typeParameters: MutableList<String>
  abstract var inherits: MutableList<String>
  abstract var members: MutableList<KotlinMember>
  abstract var functions: MutableList<KotlinFunction>
  abstract var constructorParameters: MutableMap<String, String>
}
