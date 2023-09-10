package gay.pizza.pork.evaluator

class InternalNativeProvider(val quiet: Boolean = false) : NativeProvider {
  private val functions = mutableMapOf(
    "println" to CallableFunction(::printLine)
  )

  override fun provideNativeFunction(definition: String): CallableFunction {
    return functions[definition] ?: throw RuntimeException("Unknown Internal Function: $definition")
  }

  private fun printLine(arguments: Arguments): Any {
    if (quiet) {
      return None
    }
    when (arguments.values.count()) {
      0 -> println()
      1 -> println(arguments.values[0])
      else -> println(arguments.values.joinToString(" "))
    }
    return None
  }
}
