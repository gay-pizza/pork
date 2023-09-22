package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.ArgumentSpec

class InternalNativeProvider(val quiet: Boolean = false) : NativeProvider {
  private val functions = mutableMapOf(
    "print" to CallableFunction(::printValues),
    "println" to CallableFunction(::printLine),
    "listSet" to CallableFunction(::setInList),
    "listInitWith" to CallableFunction(::listInitWith)
  )

  override fun provideNativeFunction(definition: String, arguments: List<ArgumentSpec>): CallableFunction {
    return functions[definition] ?: throw RuntimeException("Unknown Internal Function: $definition")
  }

  private fun printValues(arguments: ArgumentList, stack: CallStack): Any {
    if (quiet || arguments.isEmpty()) return None
    print(arguments.joinToString(" "))
    return None
  }

  private fun printLine(arguments: ArgumentList, stack: CallStack): Any {
    if (quiet) return None
    println(arguments.joinToString(" "))
    return None
  }

  private fun setInList(arguments: ArgumentList, stack: CallStack): Any {
    @Suppress("UNCHECKED_CAST")
    val list = arguments[0] as MutableList<Any>
    val value = arguments[2]
    list[(arguments[1] as Number).toInt()] = value
    return value
  }

  private fun listInitWith(arguments: ArgumentList, stack: CallStack): Any {
    val size = (arguments[0] as Number).toInt()
    return MutableList(size) { arguments[1] }
  }
}
