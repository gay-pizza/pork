@file:Suppress("ConstPropertyName")
package gay.pizza.pork.buildext.ast

import gay.pizza.pork.buildext.codegen.*
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
    writeNodeVisitor()
    writeNodeCoalescer()
  }

  private fun writeNodeType() {
    val enumClass = KotlinEnum(pkg, "NodeType")
    val parentMember = KotlinMember("parent", "NodeType?", value = "null")
    enumClass.members.add(parentMember)

    for (type in world.typesInDependencyOrder()) {
      val role = world.typeRegistry.roleOfType(type)

      if (role == AstTypeRole.ValueHolder || role == AstTypeRole.Enum) {
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

  private fun writeNodeVisitor() {
    val visitorInterface = KotlinClass(
      pkg,
      "NodeVisitor",
      typeParameters = mutableListOf("T"),
      isInterface = true
    )

    for (type in world.typesInDependencyOrder()) {
      val role = world.typeRegistry.roleOfType(type)

      if (role != AstTypeRole.AstNode) {
        continue
      }

      val visitFunction = KotlinFunction(
        "visit${type.name}",
        returnType = "T",
        parameters = mutableListOf(
          KotlinParameter("node", type.name)
        ),
        isInterfaceMethod = true
      )
      visitorInterface.functions.add(visitFunction)
    }
    write("NodeVisitor.kt", KotlinWriter(visitorInterface))

    val visitorExtensionSet = KotlinFunctionSet(pkg)
    val visitAnyFunction = KotlinFunction(
      "visit",
      typeParameters = mutableListOf("T"),
      extensionOf = "NodeVisitor<T>",
      returnType = "T",
      parameters = mutableListOf(
        KotlinParameter("node", type = "Node")
      ),
      isImmediateExpression = true
    )

    if (enableVisitAnyInline) {
      visitAnyFunction.inline = true
      visitAnyFunction.annotations.add("""@Suppress("NOTHING_TO_INLINE")""")
    }

    visitAnyFunction.body.add("when (node) {")
    for (type in world.typeRegistry.types.filter {
      world.typeRegistry.roleOfType(it) == AstTypeRole.AstNode
    }) {
      visitAnyFunction.body.add("  is ${type.name} -> visit${type.name}(node)")
    }
    visitAnyFunction.body.add("}")
    visitorExtensionSet.functions.add(visitAnyFunction)

    val visitNodesFunction = KotlinFunction(
      "visitNodes",
      typeParameters = mutableListOf("T"),
      extensionOf = "NodeVisitor<T>",
      returnType = "List<T>",
      parameters = mutableListOf(
        KotlinParameter("nodes", type = "Node?", vararg = true)
      ),
      isImmediateExpression = true
    )
    visitNodesFunction.body.add("nodes.asSequence().filterNotNull().map { visit(it) }.toList()")
    visitorExtensionSet.functions.add(visitNodesFunction)

    val visitAllFunction = KotlinFunction(
      "visitAll",
      typeParameters = mutableListOf("T"),
      extensionOf = "NodeVisitor<T>",
      returnType = "List<T>",
      parameters = mutableListOf(
        KotlinParameter("nodeLists", type = "List<Node?>", vararg = true)
      ),
      isImmediateExpression = true
    )
    visitAllFunction.body.add(
      "nodeLists.asSequence().flatten().filterNotNull().map { visit(it) }.toList()")
    visitorExtensionSet.functions.add(visitAllFunction)

    write("NodeVisitorExtensions.kt", KotlinWriter(visitorExtensionSet))
  }

  private fun writeNodeCoalescer() {
    val coalescerClass = KotlinClass(
      pkg,
      "NodeCoalescer",
      inherits = mutableListOf("NodeVisitor<Unit>"),
      members = mutableListOf(
        KotlinMember(
          "handler",
          "(Node) -> Unit"
        )
      )
    )

    for (type in world.typesInDependencyOrder()) {
      val role = world.typeRegistry.roleOfType(type)

      if (role != AstTypeRole.AstNode) {
        continue
      }

      val function = KotlinFunction(
        "visit${type.name}",
        returnType = "Unit",
        parameters = mutableListOf(
          KotlinParameter("node", type.name)
        ),
        isImmediateExpression = true,
        overridden = true
      )
      function.body.add("handle(node)")
      coalescerClass.functions.add(function)
    }

    val handleFunction = KotlinFunction(
      "handle",
      parameters = mutableListOf(
        KotlinParameter("node", "Node")
      )
    )
    handleFunction.body.add("handler(node)")
    handleFunction.body.add("node.visitChildren(this)")
    coalescerClass.functions.add(handleFunction)

    write("NodeCoalescer.kt", KotlinWriter(coalescerClass))
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

      val abstractVisitChildrenFunction = KotlinFunction(
        "visitChildren",
        returnType = "List<T>",
        open = true,
        typeParameters = mutableListOf("T"),
        parameters = mutableListOf(
          KotlinParameter("visitor", "NodeVisitor<T>")
        ),
        isImmediateExpression = true
      )
      abstractVisitChildrenFunction.body.add("emptyList()")
      kotlinClassLike.functions.add(abstractVisitChildrenFunction)

      val abstractVisitSelfFunction = KotlinFunction(
        "visit",
        returnType = "T",
        open = true,
        typeParameters = mutableListOf("T"),
        parameters = mutableListOf(
          KotlinParameter("visitor", "NodeVisitor<T>")
        ),
        isImmediateExpression = true
      )
      abstractVisitSelfFunction.body.add("visitor.visit(this)")
      kotlinClassLike.functions.add(abstractVisitSelfFunction)
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

    if (type.values != null) {
      for (value in type.values!!) {
        val member = KotlinMember(value.name, toKotlinType(value.typeRef))
        member.abstract = value.abstract
        if (value.defaultValue != null) {
          member.value = value.defaultValue
        }
        if (type.isParentAbstract(value)) {
          member.overridden = true
        }
        if (role == AstTypeRole.ValueHolder) {
          member.mutable = true
        }
        kotlinClassLike.members.add(member)
      }
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
      val visitChildrenFunction = KotlinFunction(
        "visitChildren",
        returnType = "List<T>",
        typeParameters = mutableListOf("T"),
        overridden = true,
        parameters = mutableListOf(
          KotlinParameter("visitor", "NodeVisitor<T>")
        ),
        isImmediateExpression = true
      )
      val anyListMembers = type.values?.any {
        it.typeRef.form == AstTypeRefForm.List
      } ?: false
      val elideVisitChildren: Boolean
      if (anyListMembers) {
        val visitParameters = (type.values?.mapNotNull {
          if (it.typeRef.primitive != null) {
            null
          } else if (it.typeRef.type != null &&
            !world.typeRegistry.roleOfType(it.typeRef.type).isNodeInherited()) {
            null
          } else if (it.typeRef.form == AstTypeRefForm.Single ||
            it.typeRef.form == AstTypeRefForm.Nullable) {
            "listOf(${it.name})"
          } else {
            it.name
          }
        } ?: emptyList()).joinToString(", ")
        elideVisitChildren = visitParameters.isEmpty()
        visitChildrenFunction.body.add("visitor.visitAll(${visitParameters})")
      } else {
        val visitParameters = (type.values?.mapNotNull {
          if (it.typeRef.primitive != null) {
            null
          } else if (it.typeRef.type != null &&
            !world.typeRegistry.roleOfType(it.typeRef.type).isNodeInherited()) {
            null
          } else {
            it.name
          }
        } ?: emptyList()).joinToString(", ")
        elideVisitChildren = visitParameters.isEmpty()
        visitChildrenFunction.body.add("visitor.visitNodes(${visitParameters})")
      }

      if (!elideVisitChildren) {
        kotlinClassLike.functions.add(visitChildrenFunction)
      }

      val visitSelfFunction = KotlinFunction(
        "visit",
        returnType = "T",
        typeParameters = mutableListOf("T"),
        overridden = true,
        parameters = mutableListOf(
          KotlinParameter("visitor", "NodeVisitor<T>")
        ),
        isImmediateExpression = true
      )
      visitSelfFunction.body.add("visitor.visit${type.name}(this)")
      kotlinClassLike.functions.add(visitSelfFunction)

      val equalsAndHashCodeMembers = kotlinClassLike.members.map {
        it.name
      }.sortedBy { it == "type" }
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
      var predicate = equalsAndHashCodeMembers.mapNotNull {
        if (it == "type") null else "other.${it} == $it"
      }.joinToString(" && ")
      if (predicate.isEmpty()) {
        predicate = "true"
      }
      equalsFunction.body.add("return $predicate")
      kotlinClassLike.functions.add(equalsFunction)

      val hashCodeFunction = KotlinFunction(
        "hashCode",
        returnType = "Int",
        overridden = true
      )

      if (equalsAndHashCodeMembers.size == 1) {
        val member = equalsAndHashCodeMembers.single()
        hashCodeFunction.isImmediateExpression = true
        hashCodeFunction.body.add("31 * ${member}.hashCode()")
      } else {
        for ((index, value) in equalsAndHashCodeMembers.withIndex()) {
          if (index == 0) {
            hashCodeFunction.body.add("var result = ${value}.hashCode()")
          } else {
            hashCodeFunction.body.add("result = 31 * result + ${value}.hashCode()")
          }
        }
        hashCodeFunction.body.add("return result")
      }
      kotlinClassLike.functions.add(hashCodeFunction)
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
    val content = "// GENERATED CODE FROM PORK AST CODEGEN\n$writer"
    val path = outputDirectory.resolve(fileName)
    path.deleteIfExists()
    path.writeText(content, StandardCharsets.UTF_8)
  }

  companion object {
    private const val enableVisitAnyInline = false

    fun run(pkg: String, world: AstWorld, outputDirectory: Path) {
      if (!outputDirectory.exists()) {
        outputDirectory.createDirectories()
      }
      val codegen = AstCodegen(pkg, outputDirectory, world)
      codegen.generate()
    }
  }
}
