package gay.pizza.pork.ffi

import gay.pizza.pork.ast.gen.ArgumentSpec
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.CompilationUnitContext
import gay.pizza.pork.evaluator.NativeProvider
import gay.pizza.pork.evaluator.None
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class JavaNativeProvider : NativeProvider {
  private val lookup = MethodHandles.lookup()

  override fun provideNativeFunction(
    definitions: List<String>,
    arguments: List<ArgumentSpec>,
    inside: CompilationUnitContext
  ): CallableFunction {
    val functionDefinition = JavaFunctionDefinition.parse(definitions)
    val javaClass = lookupClass(functionDefinition.type)
    val returnTypeClass = lookupClass(functionDefinition.returnType)
    val parameterClasses = functionDefinition.parameters.map { lookupClass(it) }
    val handle = mapKindToHandle(
      functionDefinition.kind,
      functionDefinition.symbol,
      javaClass,
      returnTypeClass,
      parameterClasses
    )
    return CallableFunction { functionArguments, _ -> handle.invokeWithArguments(functionArguments) ?: None }
  }

  private fun lookupClass(name: String): Class<*> = when (name) {
    "void" -> Void.TYPE
    "String" -> String::class.java
    "byte" -> Byte::class.java
    "char" -> Char::class.java
    "short" -> Short::class.java
    "int" -> Int::class.java
    "long" -> Long::class.java
    "float" -> Float::class.java
    "double" -> Double::class.java
    else -> lookup.findClass(name)
  }

  private fun mapKindToHandle(
    kind: String,
    symbol: String,
    javaClass: Class<*>,
    returnType: Class<*>,
    parameterTypes: List<Class<*>>
  ) = when (kind) {
    "getter" -> lookup.findGetter(javaClass, symbol, returnType)
    "setter" -> lookup.findSetter(javaClass, symbol, returnType)
    "constructor" ->
      lookup.findConstructor(javaClass, MethodType.methodType(Void.TYPE, parameterTypes))
    "static" ->
      lookup.findStatic(javaClass, symbol, MethodType.methodType(returnType, parameterTypes))
    "virtual" ->
      lookup.findVirtual(javaClass, symbol, MethodType.methodType(returnType, parameterTypes))
    "static-getter" -> lookup.findStaticGetter(javaClass, symbol, returnType)
    "static-setter" -> lookup.findStaticSetter(javaClass, symbol, returnType)
    else -> throw RuntimeException("Unknown Handle Kind: $kind")
  }
}
