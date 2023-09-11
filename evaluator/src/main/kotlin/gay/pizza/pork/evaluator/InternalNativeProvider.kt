package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.ArgumentSpec

class InternalNativeProvider(val quiet: Boolean = false) : NativeProvider {
  private val functions = mutableMapOf(
    "print" to CallableFunction(::printValues),
    "println" to CallableFunction(::printLine)
  )

  override fun provideNativeFunction(definition: String, arguments: List<ArgumentSpec>): CallableFunction {
    return functions[definition] ?: throw RuntimeException("Unknown Internal Function: $definition")
  }

  private fun printValues(arguments: Arguments): Any {
    if (quiet || arguments.values.isEmpty()) return None
    print(arguments.values.joinToString(" "))
    return None
  }

  private fun printLine(arguments: Arguments): Any {
    if (quiet) return None
    println(arguments.values.joinToString(" "))
    return None
  }
}
