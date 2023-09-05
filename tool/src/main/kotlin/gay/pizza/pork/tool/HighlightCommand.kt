package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.parser.AnsiHighlightScheme

class HighlightCommand : CliktCommand(help = "Syntax Highlighter", name = "highlight") {
  val path by argument("file")

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    print(tool.highlight(AnsiHighlightScheme()).joinToString(""))
  }
}
