package gay.pizza.pork.evaluator

import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.World

class Evaluator(val world: World, val scope: Scope) {
  private val contexts = mutableMapOf<String, CompilationUnitContext>()
  private val nativeFunctionProviders = mutableMapOf<String, NativeFunctionProvider>()

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

  fun nativeFunctionProvider(form: String): NativeFunctionProvider {
    return nativeFunctionProviders[form] ?:
      throw RuntimeException("Unknown native function form: $form")
  }

  fun addNativeFunctionProvider(form: String, nativeFunctionProvider: NativeFunctionProvider) {
    nativeFunctionProviders[form] = nativeFunctionProvider
  }
}
