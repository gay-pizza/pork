package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.ArgumentSpec
import gay.pizza.pork.execution.NativeProvider

interface ExpandedNativeProvider: NativeProvider {
  fun provideNativeFunction(definitions: List<String>, arguments: List<ArgumentSpec>, inside: SlabContext): CallableFunction {
    val function = provideNativeFunction(definitions)
    return CallableFunction { arguments, _ -> function.invoke(arguments) }
  }
}

