package gay.pizza.pork.minimal

import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.evaluator.Scope
import gay.pizza.pork.execution.ExecutionOptions
import gay.pizza.pork.execution.InternalNativeProvider
import gay.pizza.pork.execution.NativeRegistry
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  if (args.size != 1) {
    System.err.println("Usage: pork-rt <file>")
    exitProcess(1)
  }
  val path = PlatformFsProvider.resolve(args[0])
  val tool = FileTool(path)
  val nativeRegistry = NativeRegistry()
  nativeRegistry.add("internal", InternalNativeProvider(quiet = false))
  val main = tool.createExecutionContext(
    ExecutionType.Evaluator,
    Symbol("main"),
    ExecutionOptions(nativeRegistry = nativeRegistry)
  )
  main.execute()
}
