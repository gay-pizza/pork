package gay.pizza.pork.frontend

import gay.pizza.pork.parser.CharSource
import gay.pizza.pork.parser.StringCharSource
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText

class FsContentSource(val root: Path) : ContentSource {
  override fun loadAsCharSource(path: String): CharSource =
    StringCharSource(asFsPath(path).readText())

  override fun stableContentIdentity(path: String): String =
    asFsPath(path).absolutePathString()

  private fun asFsPath(path: String): Path {
    val fsPath = root.resolve(path)
    val absoluteRootPath = root.absolutePathString() + root.fileSystem.separator
    if (!fsPath.absolutePathString().startsWith(absoluteRootPath)) {
      throw RuntimeException("Unable to load path outside of the root: $fsPath (root is ${root})")
    }
    return fsPath
  }
}
