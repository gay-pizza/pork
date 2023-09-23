package gay.pizza.pork.ffi

import java.nio.file.Path
import kotlin.io.path.*

object FfiMacPlatform : FfiPlatform {
  private val frameworksDirectories = listOf(
    "/Library/Frameworks"
  )

  override fun findLibrary(name: String): Path? {
    val frameworksToCheck = frameworksDirectories.map { frameworkDirectory ->
      Path("$frameworkDirectory/$name.framework/$name")
    }
    for (framework in frameworksToCheck) {
      if (!framework.exists()) continue
      return if (framework.isSymbolicLink()) {
        return framework.parent.resolve(framework.readSymbolicLink()).absolute()
      } else {
        framework.absolute()
      }
    }
    return null
  }
}
