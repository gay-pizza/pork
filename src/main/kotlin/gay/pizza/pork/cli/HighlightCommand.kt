package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.parse.AnsiHighlightScheme

class HighlightCommand : CliktCommand(help = "Syntax Highlighter", name = "highlight") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val tool = FileTool(path)
    print(tool.highlight(AnsiHighlightScheme()).joinToString(""))
  }
}
