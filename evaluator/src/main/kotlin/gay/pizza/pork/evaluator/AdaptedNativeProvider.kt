package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.ArgumentSpec
import gay.pizza.pork.execution.NativeFunction
import gay.pizza.pork.execution.NativeProvider
import gay.pizza.pork.execution.NativeType

class AdaptedNativeProvider(val provider: NativeProvider) : ExpandedNativeProvider {
  override fun provideNativeFunction(
    definitions: List<String>,
    arguments: List<ArgumentSpec>,
    inside: SlabContext
  ): CallableFunction {
    val function = provider.provideNativeFunction(definitions)
    return CallableFunction { args, _ -> function.invoke(args) }
  }

  override fun provideNativeFunction(definitions: List<String>): NativeFunction {
    return provider.provideNativeFunction(definitions)
  }

  override fun provideNativeType(definitions: List<String>): NativeType {
    return provider.provideNativeType(definitions)
  }
}
