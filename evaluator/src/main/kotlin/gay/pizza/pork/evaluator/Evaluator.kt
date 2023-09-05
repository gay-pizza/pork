package gay.pizza.pork.evaluator

import gay.pizza.pork.frontend.World

class Evaluator(val world: World, val scope: Scope) : EvaluationContextProvider {
  private val contexts = mutableMapOf<String, EvaluationContext>()

  fun evaluate(path: String): Scope {
    val context = provideEvaluationContext(path)
    return context.externalRootScope
  }

  override fun provideEvaluationContext(path: String): EvaluationContext {
    val unit = world.load(path)
    val identity = world.contentSource.stableContentIdentity(path)
    val context = contexts.computeIfAbsent(identity) {
      EvaluationContext(unit, this, scope)
    }
    context.setup()
    return context
  }
}
