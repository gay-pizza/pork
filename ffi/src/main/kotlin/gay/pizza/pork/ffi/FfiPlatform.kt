package gay.pizza.pork.ffi

enum class FfiPlatforms(val id: String, val platform: FfiPlatform) {
  Mac("macOS", FfiMacPlatform),
  Windows("Windows", FfiWindowsPlatform),
  Unix("Unix", FfiUnixPlatform);

  companion object {
    val current by lazy {
      val operatingSystemName = System.getProperty("os.name").lowercase()
      when {
        operatingSystemName.contains("win") -> Windows
        operatingSystemName.contains("mac") -> Mac
        else -> Unix
      }
    }
  }
}

interface FfiPlatform {
  fun findLibrary(name: String): String?
}
