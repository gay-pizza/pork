package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path

class HighlightCommand : CliktCommand(help = "Syntax Highlighter", name = "highlight") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val tool = FileTool(path)
    print(tool.highlight(gay.pizza.pork.parser.AnsiHighlightScheme()).joinToString(""))
  }
}
