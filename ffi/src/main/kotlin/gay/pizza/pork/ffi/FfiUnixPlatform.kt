package gay.pizza.pork.ffi

import java.nio.file.Path

object FfiUnixPlatform : FfiPlatform {
  override fun findLibrary(name: String): String? = null
}
