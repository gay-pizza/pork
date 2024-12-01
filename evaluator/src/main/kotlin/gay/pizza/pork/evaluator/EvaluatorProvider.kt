package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.execution.ExecutionContext
import gay.pizza.pork.execution.ExecutionContextProvider
import gay.pizza.pork.execution.NativeRegistry
import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.World

class EvaluatorProvider(val world: World) : ExecutionContextProvider {
  override fun prepare(importLocator: ImportLocator, entryPointSymbol: Symbol, nativeRegistry: NativeRegistry): ExecutionContext {
    val evaluator = Evaluator(world)
    nativeRegistry.forEachProvider { form, provider ->
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
