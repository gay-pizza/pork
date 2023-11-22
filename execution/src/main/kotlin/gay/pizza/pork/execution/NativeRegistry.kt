package gay.pizza.pork.execution

class NativeRegistry {
  private val providers = mutableMapOf<String, NativeProvider>()

  fun add(form: String, provider: NativeProvider) {
    providers[form] = provider
  }

  fun forEachProvider(block: (String, NativeProvider) -> Unit) {
    for ((form, provider) in providers) {
      block(form, provider)
    }
  }

  fun of(form: String): NativeProvider =
    providers[form] ?: throw RuntimeException("Unknown native form: ${form}")
}
