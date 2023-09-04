package gay.pizza.pork.tool

import gay.pizza.pork.frontend.ContentSource
import gay.pizza.pork.frontend.FsContentSource
import gay.pizza.pork.parser.StringCharSource
import java.nio.file.Path
import kotlin.io.path.readText

class FileTool(val path: Path) : Tool() {
  override fun createCharSource(): gay.pizza.pork.parser.CharSource = StringCharSource(path.readText())
  override fun createContentSource(): ContentSource = FsContentSource(path.parent)
  override fun rootFilePath(): String = path.fileName.toString()
}
