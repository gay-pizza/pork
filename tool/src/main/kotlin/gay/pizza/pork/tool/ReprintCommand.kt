package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.minimal.FileTool

class ReprintCommand : CliktCommand("reprint") {
  val path by argument("file")

  override fun help(context: Context): String = "Reprint Parsed Compilation Unit"

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    print(tool.reprint())
  }
}
