package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.ArgumentSpec

interface NativeProvider {
  fun provideNativeFunction(definitions: List<String>, arguments: List<ArgumentSpec>): CallableFunction
}
