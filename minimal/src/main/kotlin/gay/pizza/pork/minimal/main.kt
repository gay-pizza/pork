package gay.pizza.pork.minimal

import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.evaluator.Scope
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  if (args.size != 1) {
    System.err.println("Usage: pork-rt <file>")
    exitProcess(1)
  }
  val path = PlatformFsProvider.resolve(args[0])
  val tool = FileTool(path)
  val scope = Scope()
  tool.run(scope)
}
