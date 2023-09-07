package gay.pizza.pork.evaluator

interface NativeFunctionProvider {
  fun provideNativeFunction(definition: String): CallableFunction
}
