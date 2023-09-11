package gay.pizza.pork.minimal

import gay.pizza.dough.fs.FsPath
import gay.pizza.dough.fs.readString
import gay.pizza.pork.frontend.ContentSource
import gay.pizza.pork.frontend.FsContentSource
import gay.pizza.pork.parser.CharSource
import gay.pizza.pork.parser.StringCharSource

class FileTool(val path: FsPath) : Tool() {
  override fun createCharSource(): CharSource =
    StringCharSource(path.readString())
  override fun createContentSource(): ContentSource =
    FsContentSource(path.parent!!)
  override fun rootFilePath(): String = path.fullPathString
}
