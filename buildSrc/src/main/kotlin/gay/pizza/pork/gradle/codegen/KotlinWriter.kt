package gay.pizza.pork.gradle.codegen

class KotlinWriter {
  private val buffer = StringBuilder()

  constructor(kotlinClassLike: KotlinClassLike) {
    write(kotlinClassLike)
  }

  fun writeClass(kotlinClass: KotlinClass): Unit = buffer.run {
    val classType = if (kotlinClass.sealed) "sealed class" else "class"
    writeClassLike(classType, kotlinClass)
    val members = kotlinClass.members.filter {
      it.abstract || (it.overridden && it.value != null)
    }
    if (members.isEmpty() && kotlinClass.functions.isEmpty()) {
      appendLine()
    } else {
      appendLine(" {")
    }

    for (member in members) {
      if (member.abstract) {
        appendLine("  abstract val ${member.name}: ${member.type}")
      } else {
        if (member.overridden) {
          append("  override ")
        }
        append("val ${member.name}: ${member.type}")
        if (member.value != null) {
          append(" = ")
          append(member.value)
        }
        appendLine()
      }
    }

    if (members.isNotEmpty() && kotlinClass.functions.isNotEmpty()) {
      appendLine()
    }

    writeFunctions(kotlinClass)

    if (members.isNotEmpty() || kotlinClass.functions.isNotEmpty()) {
      appendLine("}")
    }
  }

  fun writeEnum(kotlinEnum: KotlinEnum): Unit = buffer.run {
    writeClassLike("enum class", kotlinEnum)
    val membersNotCompatible = kotlinEnum.members.filter { it.abstract }
    if (membersNotCompatible.isNotEmpty()) {
      throw RuntimeException(
        "Incompatible members in enum class " +
        "${kotlinEnum.name}: $membersNotCompatible"
      )
    }

    if (kotlinEnum.entries.isEmpty() && kotlinEnum.functions.isEmpty()) {
      appendLine()
    } else {
      appendLine(" {")
    }

    for ((index, entry) in kotlinEnum.entries.withIndex()) {
      append("  ${entry.name}")
      if (entry.parameters.isNotEmpty()) {
        append("(")
        append(entry.parameters.joinToString(", "))
        append(")")
      }

      if (index != kotlinEnum.entries.size - 1) {
        append(",")
      }
      appendLine()
    }

    if (kotlinEnum.entries.isNotEmpty() && kotlinEnum.functions.isNotEmpty()) {
      appendLine()
    }

    writeFunctions(kotlinEnum)

    if (kotlinEnum.entries.isNotEmpty()) {
      appendLine("}")
    }
  }

  private fun writeClassLike(
    classType: String,
    kotlinClass: KotlinClassLike
  ): Unit = buffer.run {
    appendLine("package ${kotlinClass.pkg}")
    appendLine()

    for (import in kotlinClass.imports) {
      appendLine("import $import")
    }

    if (kotlinClass.imports.isNotEmpty()) {
      appendLine()
    }

    for (annotation in kotlinClass.annotations) {
      appendLine("@${annotation}")
    }

    append("$classType ${kotlinClass.name}")

    val contructedMembers = kotlinClass.members.filter {
      !it.abstract && !(it.overridden && it.value != null)
    }

    if (contructedMembers.isNotEmpty()) {
      val constructor = contructedMembers.joinToString(", ") {
        val prefix = if (it.overridden) "override " else ""
        val start = "${prefix}val ${it.name}: ${it.type}"
        if (it.value != null) {
          "$start = ${it.value}"
        } else start
      }
      append("(${constructor})")
    }

    if (kotlinClass.inherits.isNotEmpty()) {
      append(" : ${kotlinClass.inherits.joinToString(", ")}")
    }
  }

  private fun writeFunctions(kotlinClassLike: KotlinClassLike): Unit = buffer.run {
    for (function in kotlinClassLike.functions) {
      append("  ")

      if (function.overridden) {
        append("override ")
      }

      if (function.abstract) {
        append("abstract ")
      }

      append("fun ${function.name}(")
      append(function.parameters.joinToString(", ") {
        val start = "${it.name}: ${it.type}"
        if (it.defaultValue != null) {
          start + " = ${it.defaultValue}"
        } else start
      })
      append(")")
      if (function.returnType != null) {
        append(": ${function.returnType}")
      }

      if (!function.isImmediateExpression) {
        append(" {")
      } else {
        appendLine(" =")
      }

      if (function.body.isNotEmpty()) {
        appendLine()

        for (item in function.body) {
          appendLine("    $item")
        }
      }

      if (!function.isImmediateExpression) {
        appendLine("  }")
      }
    }
  }

  fun write(input: KotlinClassLike): Unit = when (input) {
    is KotlinClass -> writeClass(input)
    is KotlinEnum -> writeEnum(input)
    else -> throw RuntimeException("Unknown Kotlin Class Type")
  }

  override fun toString(): String = buffer.toString()
}
