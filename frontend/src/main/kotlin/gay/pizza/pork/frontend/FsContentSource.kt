package gay.pizza.pork.frontend

import gay.pizza.dough.fs.FsPath
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.dough.fs.readString
import gay.pizza.pork.tokenizer.CharSource
import gay.pizza.pork.tokenizer.StringCharSource

class FsContentSource(val root: FsPath) : ContentSource {
  override fun loadAsCharSource(path: String): CharSource =
    StringCharSource(asFsPath(path).readString())

  override fun stableContentIdentity(path: String): String =
    asFsPath(path).fullPathString

  private fun asFsPath(path: String): FsPath {
    val fsPath = root.resolve(path)
    val rootPathWithSeparator = root.fullPathString + PlatformFsProvider.separator
    if (!fsPath.fullPathString.startsWith(rootPathWithSeparator)) {
      throw RuntimeException("Unable to load path outside of the root: $fsPath (root is ${root})")
    }
    return fsPath
  }
}
