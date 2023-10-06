package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.ArgumentSpec
import gay.pizza.pork.common.unused

class InternalNativeProvider(val quiet: Boolean = false) : NativeProvider {
  private val functions = mutableMapOf(
    "print" to CallableFunction(::printValues),
    "println" to CallableFunction(::printLine),
    "listSet" to CallableFunction(::setInList),
    "listInitWith" to CallableFunction(::listInitWith)
  )

  override fun provideNativeFunction(definitions: List<String>, arguments: List<ArgumentSpec>): CallableFunction {
    val definition = definitions[0]
    return functions[definition] ?: throw RuntimeException("Unknown Internal Function: $definition")
  }

  private fun printValues(arguments: ArgumentList, stack: CallStack): Any {
    unused(stack)
    if (quiet || arguments.isEmpty()) return None
    print(arguments.joinToString(" "))
    return None
  }

  private fun printLine(arguments: ArgumentList, stack: CallStack): Any {
    unused(stack)
    if (quiet) return None
    println(arguments.joinToString(" "))
    return None
  }

  private fun setInList(arguments: ArgumentList, stack: CallStack): Any {
    unused(stack)
    @Suppress("UNCHECKED_CAST")
    val list = arguments[0] as MutableList<Any>
    val value = arguments[2]
    list[(arguments[1] as Number).toInt()] = value
    return value
  }

  private fun listInitWith(arguments: ArgumentList, stack: CallStack): Any {
    unused(stack)
    val size = (arguments[0] as Number).toInt()
    return MutableList(size) { arguments[1] }
  }
}
