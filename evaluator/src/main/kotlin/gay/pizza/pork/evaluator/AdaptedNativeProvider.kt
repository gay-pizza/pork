package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.ArgumentSpec
import gay.pizza.pork.execution.NativeProvider

class AdaptedNativeProvider(val provider: NativeProvider) : ExpandedNativeProvider {
  override fun provideNativeFunction(
    definitions: List<String>,
    arguments: List<ArgumentSpec>,
    inside: SlabContext
  ): CallableFunction {
    val function = provider.provideNativeFunction(definitions)
    return CallableFunction { args, _ -> function.invoke(args) }
  }
}
