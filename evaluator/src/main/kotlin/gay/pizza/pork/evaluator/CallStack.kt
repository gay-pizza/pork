package gay.pizza.pork.evaluator

class CallStack(val functionStack: MutableList<FunctionContext> = mutableListOf()) {
  fun push(context: FunctionContext) {
    functionStack.add(context)
  }

  fun pop() {
    functionStack.removeLast()
  }

  override fun toString(): String = buildString {
    appendLine("Pork Stacktrace:")
    for (item in functionStack.asReversed()) {
      appendLine("  at ${item.name}")
    }
  }.trimEnd()

  fun copy(): CallStack = CallStack(functionStack.toMutableList())
}
