package gay.pizza.pork.buildext.ast

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Path
import kotlin.io.path.readText

class AstWorld {
  val typeRegistry: AstTypeRegistry = AstTypeRegistry()

  fun typesInDependencyOrder(): List<AstType> {
    val typesInNameOrder = typeRegistry.types.sortedBy { it.name }
    val typesInDependencyOrder = mutableListOf<AstType>()

    fun add(type: AstType, resolving: MutableSet<AstType>) {
      if (resolving.contains(type)) {
        val cyclePath = resolving.joinToString(" ->  ") { it.name }
        throw RuntimeException("Dependency cycle detected: $cyclePath")
      }
      resolving.add(type)

      if (type.parent != null) {
        add(type.parent!!, resolving)
      }

      if (!typesInDependencyOrder.contains(type)) {
        typesInDependencyOrder.add(type)
      }
    }

    for (type in typesInNameOrder) {
      add(type, mutableSetOf())
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

        if (typeDescription.values != null) {
          type.markHasValues()
          for (value in typeDescription.values) {
            val typeRef = AstTypeRef.parse(value.type, world.typeRegistry)
            val typeValue = AstValue(value.name, typeRef, abstract = value.required)
            type.addValue(typeValue)
          }
        }

        for (enum in typeDescription.enums) {
          val astEnum = AstEnum(enum.name, enum.values)
          type.addEnum(astEnum)
        }
      }
      return world
    }

    fun read(path: Path): AstWorld {
      val astYamlText = path.readText()
      val mapper = ObjectMapper(YAMLFactory())
      mapper.registerModules(KotlinModule.Builder().build())
      val astDescription = mapper.readValue(astYamlText, AstDescription::class.java)
      return build(astDescription)
    }
  }
}
