package gay.pizza.pork.evaluator

object ScopeCache {
  private val cache = mutableListOf<Scope>()

  fun obtain(parent: Scope? = null, inherits: List<Scope> = emptyList(), name: String? = null): Scope {
    val cachedScope = cache.removeFirstOrNull()
    if (cachedScope != null) {
      cachedScope.adopt(parent = parent, inherits = inherits, name = name)
      return cachedScope
    }
    return Scope(parent = parent, inherits = inherits, name = name)
  }

  fun put(scope: Scope) {
    cache.add(scope)
  }
}
