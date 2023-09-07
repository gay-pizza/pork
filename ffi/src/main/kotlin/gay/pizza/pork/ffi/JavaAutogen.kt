package gay.pizza.pork.ffi

import gay.pizza.pork.ast.*
import java.io.PrintStream
import java.lang.reflect.Modifier

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
    for (method in javaClass.methods) {
      if (!Modifier.isPublic(method.modifiers)) {
        continue
      }

      val name = method.name
      val returnTypeName = method.returnType.name
      val parameterNames = method.parameters.indices.map { ('a' + it).toString() }
      val parameterTypeNames = method.parameters.map { it.type.name }

      fun form(kind: String): JavaFunctionDefinition =
        JavaFunctionDefinition(javaClass.name, kind, name, returnTypeName, parameterTypeNames)

      if (Modifier.isStatic(method.modifiers)) {
        definitions[name] = function(name, parameterNames, form("static"))
      } else {
        definitions[name] = function(name, parameterNames, form("virtual"))
      }
    }

    for (field in javaClass.fields) {
      if (!Modifier.isPublic(field.modifiers)) {
        continue
      }

      val name = field.name
      val valueTypeName = field.type.name
      val isStatic = Modifier.isStatic(field.modifiers)
      fun form(kind: String, getOrSet: Boolean): JavaFunctionDefinition =
        JavaFunctionDefinition(javaClass.name, kind, name, valueTypeName, if (getOrSet) {
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
        })

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
      definitions[name + "_get"] = function(name + "_get", parametersForGetter, form(getterKind, true))
      definitions[name + "_set"] = function(name + "_set", parametersForSetter, form(setterKind, false))
    }

    return definitions.values.toList()
  }

  private fun function(name: String, parameterNames: List<String>, functionDefinition: JavaFunctionDefinition): FunctionDefinition =
    FunctionDefinition(
      modifiers = DefinitionModifiers(true),
      symbol = Symbol("${prefix}_${name}"),
      arguments = parameterNames.map { Symbol(it) },
      native = asNative(functionDefinition),
      block = null
    )

  private fun asNative(functionDefinition: JavaFunctionDefinition): Native =
    Native(Symbol("java"), StringLiteral(functionDefinition.encode()))
}
