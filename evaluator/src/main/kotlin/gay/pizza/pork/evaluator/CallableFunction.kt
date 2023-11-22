package gay.pizza.pork.evaluator

import gay.pizza.pork.execution.ArgumentList

fun interface CallableFunction {
  fun call(arguments: ArgumentList, stack: CallStack): Any
}
