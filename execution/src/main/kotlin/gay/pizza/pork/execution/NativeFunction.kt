package gay.pizza.pork.execution

fun interface NativeFunction {
  fun invoke(args: ArgumentList): Any
}
