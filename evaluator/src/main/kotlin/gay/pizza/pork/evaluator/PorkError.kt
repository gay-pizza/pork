package gay.pizza.pork.evaluator

class PorkError(cause: Exception, stack: CallStack) : RuntimeException(stack.toString(), cause)
