package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.minimal.FileTool

class ParseCommand : CliktCommand("parse") {
  val loop by option("--loop", help = "Loop Parsing").flag()
  val measure by option("--measure", help = "Measure Time").flag()
  val path by argument("file")

  override fun help(context: Context): String = "Parse Compilation Unit"

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))

    maybeLoopAndMeasure(loop, measure) {
      tool.parse()
    }
  }
}
