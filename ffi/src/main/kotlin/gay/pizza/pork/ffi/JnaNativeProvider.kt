package gay.pizza.pork.ffi

import com.sun.jna.Function
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.NativeProvider

class JnaNativeProvider : NativeProvider {
  override fun provideNativeFunction(definition: String): CallableFunction {
    val functionDefinition = FfiFunctionDefinition.parse(definition)
    val function = Function.getFunction(functionDefinition.library, functionDefinition.function)
    return CallableFunction {
      invoke(function, it.values.toTypedArray(), functionDefinition.returnType)
    }
  }

  private fun invoke(function: Function, values: Array<Any>, type: String): Any = when (type) {
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
