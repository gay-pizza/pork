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
    return CallableFunction { args, _ ->
      val argumentsWithLists = mutableListOf<Any>()
      for ((index, spec) in arguments.withIndex()) {
        if (spec.multiple) {
          val list = if (index > args.size - 1) {
            listOf()
          } else {
            args.subList(index, args.size)
          }
          argumentsWithLists.add(list)
          break
        }

        if (index > args.size - 1) {
          break
        }
        val value = args[index]
        argumentsWithLists.add(value)
      }
      function.invoke(argumentsWithLists)
    }
  }

  override fun provideNativeFunction(definitions: List<String>): NativeFunction {
    return provider.provideNativeFunction(definitions)
  }

  override fun provideNativeType(definitions: List<String>): NativeType {
    return provider.provideNativeType(definitions)
  }
}
