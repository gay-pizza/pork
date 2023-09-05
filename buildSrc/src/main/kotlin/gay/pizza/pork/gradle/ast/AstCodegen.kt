package gay.pizza.pork.gradle.ast

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import gay.pizza.pork.gradle.codegen.*
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.*

class AstCodegen(val pkg: String, val outputDirectory: Path, val world: AstWorld) {
  private fun deleteAllContents() {
    for (child in outputDirectory.listDirectoryEntries("*.kt")) {
      child.deleteExisting()
    }
  }

  fun generate() {
    deleteAllContents()
    for (type in world.typeRegistry.types) {
      writeAstType(type)
    }
    writeNodeType()
  }

  private fun writeNodeType() {
    val enumClass = KotlinEnum(pkg, "NodeType")
    val parentMember = KotlinMember("parent", "NodeType?", value = "null")
    enumClass.members.add(parentMember)

    val typesInNameOrder = world.typeRegistry.types.sortedBy { it.name }
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

    for (type in typesInDependencyOrder) {
      val role = world.typeRegistry.roleOfType(type)

      if (role == AstTypeRole.ValueHolder || role == AstTypeRole.Enum) {
        println(type)
        continue
      }

      val entry = KotlinEnumEntry(type.name)
      if (type.parent != null) {
        entry.parameters.add(type.parent!!.name)
      }
      enumClass.entries.add(entry)
    }

    write("NodeType.kt", KotlinWriter(enumClass))
  }

  private fun writeAstType(type: AstType) {
    val role = world.typeRegistry.roleOfType(type)

    val kotlinClassLike: KotlinClassLike
    if (role == AstTypeRole.Enum) {
      val kotlinEnum = KotlinEnum(pkg, type.name)
      kotlinClassLike = kotlinEnum
    } else {
      val kotlinClass = KotlinClass(pkg, type.name)
      kotlinClassLike = kotlinClass
      if (role == AstTypeRole.RootNode || role == AstTypeRole.HierarchyNode) {
        kotlinClass.sealed = true
      }
    }

    if (role == AstTypeRole.RootNode) {
      val typeMember = KotlinMember(
        "type",
        "NodeType",
        abstract = true
      )
      kotlinClassLike.members.add(typeMember)
    } else if (role == AstTypeRole.AstNode) {
      val typeMember = KotlinMember(
        "type",
        "NodeType",
        overridden = true,
        value = "NodeType.${type.name}"
      )
      kotlinClassLike.members.add(typeMember)
    }

    if (type.parent != null) {
      val parentName = type.parent!!.name
      kotlinClassLike.inherits.add("$parentName()")
    }

    for (value in type.values) {
      val member = KotlinMember(value.name, toKotlinType(value.typeRef))
      member.abstract = value.abstract
      if (type.isParentAbstract(value)) {
        member.overridden = true
      }
      kotlinClassLike.members.add(member)
    }

    if (role == AstTypeRole.Enum) {
      val kotlinEnum = kotlinClassLike as KotlinEnum
      for (entry in type.enums) {
        val orderOfKeys = entry.values.keys.sortedBy { key ->
          kotlinClassLike.members.indexOfFirst { it.name == key }
        }

        val parameters = mutableListOf<String>()
        for (key in orderOfKeys) {
          val value = entry.values[key] ?: continue
          parameters.add("\"${value}\"")
        }
        val enumEntry = KotlinEnumEntry(entry.name, parameters)
        kotlinEnum.entries.add(enumEntry)
      }
    }

    if (role == AstTypeRole.AstNode) {
      val equalsAndHashCodeFields = kotlinClassLike.members.map { it.name }
      val equalsFunction = KotlinFunction(
        "equals",
        returnType = "Boolean",
        overridden = true
      )
      equalsFunction.parameters.add(KotlinParameter(
        "other",
        "Any?"
      ))
      equalsFunction.body.add("if (other !is ${type.name}) return false")
      val predicate = equalsAndHashCodeFields.joinToString(" && ") {
        "other.${it} == $it"
      }
      equalsFunction.body.add("return $predicate")
      kotlinClassLike.functions.add(equalsFunction)
    }

    val serialName = kotlinClassLike.name[0].lowercase() +
      kotlinClassLike.name.substring(1)
    kotlinClassLike.imports.add("kotlinx.serialization.SerialName")
    kotlinClassLike.imports.add("kotlinx.serialization.Serializable")
    kotlinClassLike.annotations.add("Serializable")
    kotlinClassLike.annotations.add("SerialName(\"$serialName\")")

    write("${type.name}.kt", KotlinWriter(kotlinClassLike))
  }

  private fun toKotlinType(typeRef: AstTypeRef): String {
    val baseType = typeRef.type?.name ?: typeRef.primitive?.id
      ?: throw RuntimeException("Unable to determine base type.")
    return when (typeRef.form) {
      AstTypeRefForm.Single -> baseType
      AstTypeRefForm.Nullable -> "${baseType}?"
      AstTypeRefForm.List -> "List<${baseType}>"
    }
  }

  private fun write(fileName: String, writer: KotlinWriter) {
    val path = outputDirectory.resolve(fileName)
    path.deleteIfExists()
    path.writeText(writer.toString(), StandardCharsets.UTF_8)
  }

  companion object {
    fun run(pkg: String, astDescriptionFile: Path, outputDirectory: Path) {
      val astYamlText = astDescriptionFile.readText()
      val mapper = ObjectMapper(YAMLFactory())
      mapper.registerModules(KotlinModule.Builder().build())
      val astDescription = mapper.readValue(astYamlText, AstDescription::class.java)
      val world = AstWorld.build(astDescription)
      val codegen = AstCodegen(pkg, outputDirectory, world)
      codegen.generate()
    }
  }
}
