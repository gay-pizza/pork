package gay.pizza.pork.ffi

import gay.pizza.pork.ast.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter

class JavaAutogen(val javaClass: Class<*>) {
  private val prefix = javaClass.name.replace(".", "_")

  fun generateCompilationUnit(): CompilationUnit {
    return CompilationUnit(
      declarations = listOf(),
      definitions = generateFunctionDefinitions()
    )
  }

  fun generateFunctionDefinitions(): List<FunctionDefinition> {
    val definitions = mutableMapOf<String, FunctionDefinition>()

    val methodGroups = mutableMapOf<String, MutableList<Method>>()
    for (method in javaClass.methods) {
      if (!Modifier.isPublic(method.modifiers)) {
        continue
      }
      methodGroups.getOrPut(method.name) { mutableListOf() }.add(method)
    }

    for ((baseName, methods) in methodGroups) {
      for (method in methods) {
        var name = baseName
        if (methods.size > 1 && method.parameters.isNotEmpty()) {
          name += "_" + method.parameters.joinToString("_") {
            discriminate(it)
          }
        }
        val returnTypeName = method.returnType.name
        val parameterNames = method.parameters.indices.map { ('a' + it).toString() }
        val parameterTypeNames = method.parameters.map { it.type.name }

        fun form(kind: String): JavaFunctionDefinition =
          JavaFunctionDefinition(
            javaClass.name,
            kind,
            method.name,
            returnTypeName,
            parameterTypeNames
          )

        if (Modifier.isStatic(method.modifiers)) {
          definitions[name] = function(name, parameterNames, form("static"))
        } else {
          definitions[name] = function(name, parameterNames, form("virtual"))
        }
      }

      for (constructor in javaClass.constructors) {
        val parameterNames = constructor.parameters.indices.map { ('a' + it).toString() }
        val parameterTypeNames = constructor.parameters.map { it.type.name }

        var name = "new"
        if (javaClass.constructors.isNotEmpty()) {
          name += "_" + constructor.parameters.joinToString("_") {
            discriminate(it)
          }
        }

        val javaFunctionDefinition = JavaFunctionDefinition(
          javaClass.name,
          "constructor",
          "new",
          javaClass.name,
          parameterTypeNames
        )
        definitions[name] = function(name, parameterNames, javaFunctionDefinition)
      }
    }

    for (field in javaClass.fields) {
      if (!Modifier.isPublic(field.modifiers)) {
        continue
      }

      val name = field.name
      val valueTypeName = field.type.name
      val isStatic = Modifier.isStatic(field.modifiers)
      fun form(kind: String, getOrSet: Boolean): JavaFunctionDefinition {
        val parameters = if (getOrSet) {
          if (isStatic) {
            emptyList()
          } else {
            listOf(javaClass.name)
          }
        } else {
          if (isStatic) {
            listOf(valueTypeName)
          } else {
            listOf(javaClass.name, valueTypeName)
          }
        }
        return JavaFunctionDefinition(
          javaClass.name,
          kind,
          name,
          valueTypeName,
          parameters
        )
      }

      val parametersForGetter = if (isStatic) {
        emptyList()
      } else {
        listOf("object")
      }

      val parametersForSetter = if (isStatic) {
        listOf("value")
      } else {
        listOf("object", "value")
      }

      val getterKind = if (isStatic) "static-getter" else "getter"
      val setterKind = if (isStatic) "static-setter" else "setter"
      definitions[name + "_get"] = function(
        name + "_get",
        parametersForGetter,
        form(getterKind, true)
      )
      definitions[name + "_set"] = function(
        name + "_set",
        parametersForSetter,
        form(setterKind, false)
      )
    }

    return definitions.values.toList()
  }

  private fun function(
    name: String,
    parameterNames: List<String>,
    functionDefinition: JavaFunctionDefinition
  ): FunctionDefinition =
    FunctionDefinition(
      modifiers = DefinitionModifiers(true),
      symbol = Symbol("${prefix}_${name}"),
      arguments = parameterNames.map {
        ArgumentSpec(
          symbol = Symbol(it),
          multiple = false
        )
      },
      native = asNative(functionDefinition),
      block = null
    )

  private fun asNative(functionDefinition: JavaFunctionDefinition): Native =
    Native(Symbol("java"), functionDefinition.encode().map { StringLiteral(it) })

  private fun discriminate(parameter: Parameter): String =
    parameter.type.simpleName.lowercase().replace("[]", "_array")
}
