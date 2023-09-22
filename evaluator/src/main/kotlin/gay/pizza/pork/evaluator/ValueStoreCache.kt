package gay.pizza.pork.evaluator

object ValueStoreCache {
  private val cache = mutableListOf<ValueStore>()

  fun obtain(value: Any, type: ValueStoreType): ValueStore {
    val cached = cache.removeFirstOrNull()
    if (cached != null) {
      cached.adopt(value, type)
      return cached
    }
    return ValueStore(value, type)
  }

  fun put(store: ValueStore) {
    cache.add(store)
  }
}
