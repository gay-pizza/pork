package gay.pizza.pork.ffi

import java.nio.file.Path

object FfiWindowsPlatform : FfiPlatform {
  override fun findLibrary(name: String): String? = null
}
