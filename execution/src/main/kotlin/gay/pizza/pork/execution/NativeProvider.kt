package gay.pizza.pork.execution

interface NativeProvider {
  fun provideNativeFunction(definitions: List<String>): NativeFunction
}
