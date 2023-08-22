package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.parse.AnsiHighlightScheme
import gay.pizza.pork.parse.Highlighter
import gay.pizza.pork.parse.PorkTokenizer
import gay.pizza.pork.parse.StringCharSource
import kotlin.io.path.readText

class HighlightCommand : CliktCommand(help = "Syntax Highlighter", name = "highlight") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val content = path.readText()
    val tokenStream = PorkTokenizer(StringCharSource(content)).tokenize()
    val highlighter = Highlighter(AnsiHighlightScheme())
    print(highlighter.highlight(tokenStream).joinToString(""))
  }
}
