package gay.pizza.pork.compiler

class ComputableState<X, T>(val computation: (X) -> T) {
  private val state = StoredState<X, T>()

  fun of(key: X): T = state.computeIfAbsent(key, computation)
}
