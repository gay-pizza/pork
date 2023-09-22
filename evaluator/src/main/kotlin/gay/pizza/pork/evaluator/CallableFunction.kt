package gay.pizza.pork.evaluator

fun interface CallableFunction {
  fun call(arguments: ArgumentList, stack: CallStack): Any
}
