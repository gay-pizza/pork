package gay.pizza.pork.evaluator

import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.Slab
import gay.pizza.pork.frontend.World

class Evaluator(val world: World) {
  private val scope = Scope.root()
  private val contexts = mutableMapOf<Slab, SlabContext>()
  private val nativeProviders = mutableMapOf<String, ExpandedNativeProvider>()

  fun evaluate(locator: ImportLocator): Scope {
    val slabContext = slabContext(locator)
    slabContext.finalizeScope()
    return slabContext.externalScope
  }

  fun slabContext(slab: Slab): SlabContext {
    val slabContext = contexts.computeIfAbsent(slab) {
      SlabContext(slab, this, scope)
    }
    slabContext.ensureImportedContextsExist()
    return slabContext
  }

  fun slabContext(locator: ImportLocator): SlabContext = slabContext(world.load(locator))

  fun nativeFunctionProvider(form: String): ExpandedNativeProvider {
    return nativeProviders[form] ?:
      throw RuntimeException("Unknown native function form: $form")
  }

  fun addNativeProvider(form: String, nativeProvider: ExpandedNativeProvider) {
    nativeProviders[form] = nativeProvider
  }
}
