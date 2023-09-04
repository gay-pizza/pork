package gay.pizza.pork.gradle.ast

class AstWorld {
  val typeRegistry: AstTypeRegistry = AstTypeRegistry()

  fun build(description: AstDescription) {
    val rootType = typeRegistry.add(AstType(description.root))
    for (typeName in description.types.keys) {
      if (typeName == rootType.name) {
        throw RuntimeException("Cannot have type with the same name as the root type.")
      }

      typeRegistry.add(AstType(typeName))
    }

    for ((typeName, typeDescription) in description.types) {
      val type = typeRegistry.lookup(typeName)
      if (typeDescription.parent != null) {
        type.parent = typeRegistry.lookup(typeDescription.parent)
      }
      for (value in typeDescription.values) {
        val typeRef = AstTypeRef.parse(value.type, typeRegistry)
        val typeValue = AstValue(value.name, typeRef)
        type.addValue(typeValue)
      }
    }
  }
}
