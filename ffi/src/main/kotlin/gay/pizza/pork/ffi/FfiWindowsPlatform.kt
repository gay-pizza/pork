package gay.pizza.pork.ffi

object FfiWindowsPlatform : FfiPlatform {
  override fun findLibrary(name: String): String? = null
}
