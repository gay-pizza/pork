package gay.pizza.pork.evaluator

class FastVariableCache {
  private val cache = mutableMapOf<String, Any>()

  fun put(key: String, value: Any) {
    cache[key] = value
  }

  fun lookup(key: String): Any = cache[key] ?: NotFound
  fun has(key: String): Boolean = cache.containsKey(key)

  fun invalidate(key: String) {
    cache.remove(key)
  }

  object NotFound
}
