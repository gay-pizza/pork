package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.ArgumentSpec

interface NativeProvider {
  fun provideNativeFunction(definitions: List<String>, arguments: List<ArgumentSpec>, inside: CompilationUnitContext): CallableFunction
}
