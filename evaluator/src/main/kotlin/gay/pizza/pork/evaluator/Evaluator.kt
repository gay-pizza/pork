package gay.pizza.pork.evaluator

import gay.pizza.pork.frontend.World

class Evaluator(val world: World, val scope: Scope) {
  private val contexts = mutableMapOf<String, CompilationUnitContext>()

  fun evaluate(path: String): Scope =
    context(path).externalScope

  fun context(path: String): CompilationUnitContext {
    val unit = world.load(path)
    val identity = world.contentSource.stableContentIdentity(path)
    val context = contexts.computeIfAbsent(identity) {
      CompilationUnitContext(unit, this, scope)
    }
    context.initIfNeeded()
    return context
  }
}
