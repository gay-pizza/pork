package gay.pizza.pork.ffi

object FfiUnixPlatform : FfiPlatform {
  override fun findLibrary(name: String): String? = null
}
