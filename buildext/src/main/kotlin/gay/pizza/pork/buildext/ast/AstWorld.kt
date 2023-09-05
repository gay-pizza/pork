package gay.pizza.pork.buildext.ast

class AstWorld {
  val typeRegistry: AstTypeRegistry = AstTypeRegistry()

  fun typesInDependencyOrder(): List<AstType> {
    val typesInNameOrder = typeRegistry.types.sortedBy { it.name }
    val typesInDependencyOrder = mutableListOf<AstType>()
    for (type in typesInNameOrder) {
      if (type.parent != null) {
        if (!typesInDependencyOrder.contains(type.parent)) {
          typesInDependencyOrder.add(type.parent!!)
        }
      }

      if (!typesInDependencyOrder.contains(type)) {
        typesInDependencyOrder.add(type)
      }
    }
    return typesInDependencyOrder
  }

  companion object {
    fun build(description: AstDescription): AstWorld {
      val world = AstWorld()
      val rootType = world.typeRegistry.add(AstType(description.root))
      for (typeName in description.types.keys) {
        if (typeName == rootType.name) {
          throw RuntimeException("Cannot have type with the same name as the root type.")
        }

        world.typeRegistry.add(AstType(typeName))
      }

      for ((typeName, typeDescription) in description.types) {
        val type = world.typeRegistry.lookup(typeName)

        if (typeDescription.parent != null) {
          type.parent = world.typeRegistry.lookup(typeDescription.parent)
        }

        for (value in typeDescription.values) {
          val typeRef = AstTypeRef.parse(value.type, world.typeRegistry)
          val typeValue = AstValue(value.name, typeRef, abstract = value.required)
          type.addValue(typeValue)
        }

        for (enum in typeDescription.enums) {
          val astEnum = AstEnum(enum.name, enum.values)
          type.addEnum(astEnum)
        }
      }
      return world
    }
  }
}
