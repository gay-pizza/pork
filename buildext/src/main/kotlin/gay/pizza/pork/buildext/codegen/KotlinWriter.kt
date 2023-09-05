package gay.pizza.pork.buildext.codegen

class KotlinWriter() {
  private val buffer = StringBuilder()

  constructor(writable: Any) : this() {
    write(writable)
  }

  fun writeClass(kotlinClass: KotlinClass): Unit = buffer.run {
    val classType = when {
      kotlinClass.sealed -> "sealed class"
      kotlinClass.isInterface -> "interface"
      else -> "class"
    }
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
      val form = if (member.mutable) "var" else "val"
      if (member.abstract) {
        appendLine("  abstract $form ${member.name}: ${member.type}")
      } else {
        if (member.overridden) {
          append("  override ")
        }
        append("$form ${member.name}: ${member.type}")
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
    writeHeader(kotlinClass.pkg, kotlinClass.imports)

    for (annotation in kotlinClass.annotations) {
      appendLine("@${annotation}")
    }

    append("$classType ${kotlinClass.name}")
    if (kotlinClass.typeParameters.isNotEmpty()) {
      val typeParameters = kotlinClass.typeParameters.joinToString(", ")
      append("<${typeParameters}>")
    }

    val contructedMembers = kotlinClass.members.filter {
      !it.abstract && !(it.overridden && it.value != null)
    }

    if (contructedMembers.isNotEmpty()) {
      val constructor = contructedMembers.joinToString(", ") {
        val prefix = if (it.overridden) "override " else ""
        val form = if (it.mutable) "var" else "val"
        val start = "${prefix}$form ${it.name}: ${it.type}"
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

  private fun writeHeader(pkg: String, imports: List<String>): Unit = buffer.run {
    appendLine("package $pkg")
    appendLine()

    for (import in imports) {
      appendLine("import $import")
    }

    if (imports.isNotEmpty()) {
      appendLine()
    }
  }

  fun writeFunction(function: KotlinFunction, index: Int = 0, functionCount: Int = 1, indent: String = ""): Unit = buffer.run {
    append(indent)

    if (function.overridden) {
      append("override ")
    }

    if (function.abstract) {
      append("abstract ")
    }

    if (function.open) {
      append("open ")
    }

    append("fun ")
    if (function.typeParameters.isNotEmpty()) {
      append("<${function.typeParameters.joinToString(", ")}> ")
    }

    if (function.extensionOf != null) {
      append("${function.extensionOf}.")
    }

    append("${function.name}(")
    append(function.parameters.joinToString(", ") {
      var start = "${it.name}: ${it.type}"
      if (it.vararg) {
        start = "vararg $start"
      }
      if (it.defaultValue != null) {
        start + " = ${it.defaultValue}"
      } else start
    })
    append(")")
    if (function.returnType != null) {
      append(": ${function.returnType}")
    }

    if (!function.isImmediateExpression && !function.abstract && !function.isInterfaceMethod) {
      append(" {")
    } else if (!function.abstract && !function.isInterfaceMethod) {
      append(" =")
    }

    if (function.body.isNotEmpty()) {
      appendLine()

      for (item in function.body) {
        appendLine("$indent  $item")
      }
    }

    if (!function.isImmediateExpression && !function.abstract && !function.isInterfaceMethod) {
      if (function.body.isNotEmpty()) {
        append(indent)
      }
      appendLine("}")
    }

    if (function.abstract || function.isInterfaceMethod) {
      appendLine()
    }

    if (index < functionCount - 1) {
      appendLine()
    }
  }

  fun writeFunctions(kotlinClassLike: KotlinClassLike): Unit = buffer.run {
    for ((index, function) in kotlinClassLike.functions.withIndex()) {
      writeFunction(function, index = index, functionCount = kotlinClassLike.functions.size, indent = "  ")
    }
  }

  fun writeFunctionSet(functionSet: KotlinFunctionSet) {
    writeHeader(functionSet.pkg, functionSet.imports)
    for ((index, function) in functionSet.functions.withIndex()) {
      writeFunction(
        function,
        index = index,
        functionCount = functionSet.functions.size
      )
    }
  }

  fun write(input: Any): Unit = when (input) {
    is KotlinClass -> writeClass(input)
    is KotlinEnum -> writeEnum(input)
    is KotlinFunctionSet -> writeFunctionSet(input)
    else -> throw RuntimeException("Unknown Kotlin Type")
  }

  override fun toString(): String = buffer.toString()
}
