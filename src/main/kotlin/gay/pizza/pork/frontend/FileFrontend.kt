package gay.pizza.pork.frontend

import gay.pizza.pork.ast.NodeVisitor
import gay.pizza.pork.parse.CharSource
import gay.pizza.pork.parse.StringCharSource
import java.nio.file.Path
import kotlin.io.path.readText

class FileFrontend(val path: Path) : Frontend() {
  override fun createCharSource(): CharSource = StringCharSource(path.readText())
}
