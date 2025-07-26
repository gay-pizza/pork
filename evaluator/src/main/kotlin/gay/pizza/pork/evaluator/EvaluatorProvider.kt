package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.execution.ExecutionContext
import gay.pizza.pork.execution.ExecutionContextProvider
import gay.pizza.pork.execution.ExecutionOptions
import gay.pizza.pork.execution.NativeRegistry
import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.World

class EvaluatorProvider(val world: World) : ExecutionContextProvider {
  override fun prepare(importLocator: ImportLocator, entryPointSymbol: Symbol, options: ExecutionOptions): ExecutionContext {
    val evaluator = Evaluator(world)
    options.nativeRegistry.forEachProvider { form, provider ->
      if (provider is ExpandedNativeProvider) {
        evaluator.addNativeProvider(form, provider)
      } else {
        evaluator.addNativeProvider(form, AdaptedNativeProvider(provider))
      }
    }
    val slab = evaluator.slabContext(importLocator)
    slab.finalizeScope()
    return EvaluatorExecutionContext(evaluator, slab, entryPointSymbol)
  }
}
