package gay.pizza.pork.evaluator

import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.World

class Evaluator(val world: World, val scope: Scope) {
  private val contexts = mutableMapOf<String, CompilationUnitContext>()
  private val nativeProviders = mutableMapOf<String, NativeProvider>()

  fun evaluate(locator: ImportLocator): Scope =
    context(locator).externalScope

  fun context(locator: ImportLocator): CompilationUnitContext {
    val unit = world.load(locator)
    val identity = world.stableIdentity(locator)
    val context = contexts.computeIfAbsent(identity) {
      CompilationUnitContext(unit, this, scope, name = "${locator.form} ${locator.path}")
    }
    context.initIfNeeded()
    return context
  }

  fun nativeFunctionProvider(form: String): NativeProvider {
    return nativeProviders[form] ?:
      throw RuntimeException("Unknown native function form: $form")
  }

  fun addNativeProvider(form: String, nativeProvider: NativeProvider) {
    nativeProviders[form] = nativeProvider
  }
}
