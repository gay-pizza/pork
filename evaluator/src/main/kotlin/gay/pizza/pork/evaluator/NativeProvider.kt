package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.ArgumentSpec

interface NativeProvider {
  fun provideNativeFunction(definition: String, arguments: List<ArgumentSpec>): CallableFunction
}
