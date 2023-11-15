package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.execution.ExecutionContext
import gay.pizza.pork.execution.ExecutionContextProvider
import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.Slab
import gay.pizza.pork.frontend.World

class Evaluator(val world: World) : ExecutionContextProvider {
  private val scope = Scope.root()
  private val contexts = mutableMapOf<Slab, SlabContext>()
  private val nativeProviders = mutableMapOf<String, NativeProvider>()

  fun evaluate(locator: ImportLocator): Scope {
    val slabContext = context(locator)
    slabContext.finalizeScope()
    return slabContext.externalScope
  }

  fun context(slab: Slab): SlabContext {
    val slabContext = contexts.computeIfAbsent(slab) {
      SlabContext(slab, this, scope)
    }
    slabContext.ensureImportedContextsExist()
    return slabContext
  }

  fun context(locator: ImportLocator): SlabContext = context(world.load(locator))

  fun nativeFunctionProvider(form: String): NativeProvider {
    return nativeProviders[form] ?:
      throw RuntimeException("Unknown native function form: $form")
  }

  fun addNativeProvider(form: String, nativeProvider: NativeProvider) {
    nativeProviders[form] = nativeProvider
  }

  override fun prepare(importLocator: ImportLocator, entryPointSymbol: Symbol): ExecutionContext {
    val slab = context(importLocator)
    slab.finalizeScope()
    return EvaluatorExecutionContext(this, slab, entryPointSymbol)
  }
}
