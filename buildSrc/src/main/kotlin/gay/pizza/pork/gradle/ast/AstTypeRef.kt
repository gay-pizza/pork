package gay.pizza.pork.gradle.ast

class AstTypeRef(
  val type: AstType? = null,
  val primitive: AstPrimitive? = null,
  val form: AstTypeRefForm
) {
  companion object {
    fun parse(input: String, registry: AstTypeRegistry): AstTypeRef {
      if (input.startsWith("List<")) {
        val underlyingType = input.substring(5, input.length - 1)
        val underlyingRef = parse(underlyingType, registry)
        return AstTypeRef(
          type = underlyingRef.type,
          primitive = underlyingRef.primitive,
          form = AstTypeRefForm.List
        )
      }

      var form = AstTypeRefForm.Single
      var typeName: String = input
      if (input.endsWith("?")) {
        form = AstTypeRefForm.Nullable
        typeName = input.substring(0, input.length - 1)
      }

      val primitive = AstPrimitive.values().firstOrNull { it.name == typeName }
      if (primitive != null) {
        return AstTypeRef(
          primitive = primitive,
          form = form
        )
      }

      return AstTypeRef(type = registry.lookup(typeName), form = form)
    }
  }
}
