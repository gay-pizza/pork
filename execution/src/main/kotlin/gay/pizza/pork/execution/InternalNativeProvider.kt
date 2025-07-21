package gay.pizza.pork.execution

class InternalNativeProvider(val quiet: Boolean = false) : NativeProvider {
  private val functions = mutableMapOf(
    "print" to NativeFunction(::printValues),
    "println" to NativeFunction(::printLine),
    "listSet" to NativeFunction(::setInList),
    "listInitWith" to NativeFunction(::listInitWith)
  )

  private val types = mutableMapOf(
    "int32" to NativeType { Int::class.java },
    "int64" to NativeType { Long::class.java },
    "string" to NativeType { String::class.java },
    "float32" to NativeType { Float::class.java },
    "float64" to NativeType { Double::class.java },
    "bool" to NativeType { Boolean::class.java },
  )

  fun add(name: String, function: NativeFunction) {
    functions[name] = function
  }

  fun add(name: String, type: NativeType) {
    types[name] = type
  }

  override fun provideNativeFunction(definitions: List<String>): NativeFunction {
    val definition = definitions[0]
    return functions[definition] ?:
      throw RuntimeException("Unknown internal function: $definition")
  }

  override fun provideNativeType(definitions: List<String>): NativeType {
    val definition = definitions[0]
    return types[definition] ?:
      throw RuntimeException("Unknown internal type: $definition")
  }

  private fun printValues(arguments: ArgumentList): Any {
    if (quiet || arguments.isEmpty()) return None
    print(arguments.at<List<*>>(0).joinToString(" ") { it.toString() })
    return None
  }

  private fun printLine(arguments: ArgumentList): Any {
    if (quiet) return None
    println(arguments.at<List<*>>(0).joinToString(" ") { it.toString() })
    return Unit
  }

  private fun setInList(arguments: ArgumentList): Any {
    @Suppress("UNCHECKED_CAST")
    val list = arguments[0] as MutableList<Any>
    val value = arguments[2]
    list[(arguments.at<Number>(1)).toInt()] = value
    return value
  }

  private fun listInitWith(arguments: ArgumentList): Any {
    val size = arguments.at<Number>(0).toInt()
    return MutableList(size) { arguments[1] }
  }
}
