package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.ArgumentSpec

interface ExpandedNativeProvider {
  fun provideNativeFunction(definitions: List<String>, arguments: List<ArgumentSpec>, inside: SlabContext): CallableFunction
}
