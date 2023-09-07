package gay.pizza.pork.ffi

import com.sun.jna.Function
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.NativeFunctionProvider

class JnaNativeProvider : NativeFunctionProvider {
  override fun provideNativeFunction(definition: String): CallableFunction {
    val (libraryName, functionSymbol, returnType, _) =
      definition.split(":", limit = 3)
    val function = Function.getFunction(libraryName, functionSymbol)
    return CallableFunction {
      return@CallableFunction invoke(function, it.values.toTypedArray(), returnType)
    }
  }

  private fun invoke(function: Function, values: Array<Any>, type: String): Any = when (type) {
    "void*" -> function.invokePointer(values)
    "int" -> function.invokeInt(values)
    "long" -> function.invokeLong(values)
    "float" -> function.invokeFloat(values)
    "double" -> function.invokeDouble(values)
    "void" -> function.invokeVoid(values)
    else -> throw RuntimeException("Unsupported ffi return type: $type")
  }
}
