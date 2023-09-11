package gay.pizza.pork.ffi

import com.sun.jna.Function
import gay.pizza.pork.ast.ArgumentSpec
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.NativeProvider

class JnaNativeProvider : NativeProvider {
  override fun provideNativeFunction(definition: String, arguments: List<ArgumentSpec>): CallableFunction {
    val functionDefinition = FfiFunctionDefinition.parse(definition)
    val function = Function.getFunction(functionDefinition.library, functionDefinition.function)
    return CallableFunction { functionArgs ->
      val ffiArgs = mutableListOf<Any?>()
      for ((index, spec) in arguments.withIndex()) {
        if (spec.multiple) {
          val variableArguments = functionArgs.values
            .subList(index, functionArgs.values.size)
          ffiArgs.addAll(variableArguments)
          break
        } else {
          ffiArgs.add(functionArgs.values[index])
        }
      }
      invoke(function, ffiArgs.toTypedArray(), functionDefinition.returnType)
    }
  }

  private fun invoke(function: Function, values: Array<Any?>, type: String): Any = when (type) {
    "void*" -> function.invokePointer(values)
    "int" -> function.invokeInt(values)
    "long" -> function.invokeLong(values)
    "float" -> function.invokeFloat(values)
    "double" -> function.invokeDouble(values)
    "void" -> function.invokeVoid(values)
    "char*" -> function.invokeString(values, false)
    else -> throw RuntimeException("Unsupported ffi return type: $type")
  }
}
