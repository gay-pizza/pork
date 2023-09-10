package gay.pizza.pork.evaluator

interface NativeProvider {
  fun provideNativeFunction(definition: String): CallableFunction
}
