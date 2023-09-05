package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import gay.pizza.dough.fs.PlatformFsProvider

class ParseCommand : CliktCommand(help = "Parse Compilation Unit", name = "parse") {
  val loop by option("--loop", help = "Loop Parsing").flag()
  val measure by option("--measure", help = "Measure Time").flag()
  val path by argument("file")

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))

    maybeLoopAndMeasure(loop, measure) {
      tool.parse()
    }
  }
}
