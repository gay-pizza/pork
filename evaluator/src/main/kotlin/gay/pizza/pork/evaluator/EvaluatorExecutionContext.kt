package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.execution.ExecutionContext

class EvaluatorExecutionContext(val evaluator: Evaluator, val slab: SlabContext, val entryPointSymbol: Symbol) : ExecutionContext {
  private val function: CallableFunction by lazy {
    val value = slab.externalScope.value(entryPointSymbol.id)
    if (value !is CallableFunction) {
      throw RuntimeException("Symbol '${entryPointSymbol.id}' resolves to a non-function.")
    }
    value
  }

  override fun execute() {
    function.call(emptyList(), CallStack())
  }
}
