package gay.pizza.pork.compiler

class StoredState<X, T> {
  private val state = mutableMapOf<X, T>()

  fun computeIfAbsent(key: X, computation: (X) -> T): T = state.computeIfAbsent(key, computation)
  fun of(key: X): T? = state[key]
  fun put(key: X, value: T) {
    state[key] = value
  }
}
