package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.execution.ExecutionOptions
import gay.pizza.pork.execution.InternalNativeProvider
import gay.pizza.pork.execution.NativeRegistry
import gay.pizza.pork.ffi.FfiNativeProvider
import gay.pizza.pork.ffi.JavaNativeProvider
import gay.pizza.pork.minimal.ExecutionType
import gay.pizza.pork.minimal.FileTool

class RunCommand : CliktCommand("run") {
  val loop by option("--loop", help = "Loop Program").flag()
  val measure by option("--measure", help = "Measure Time").flag()
  val quiet by option("--quiet", help = "Silence Prints").flag()
  val debug by option("--debug", help = "Debug Mode").flag()
  val executionType by option("--execution-type", "-E")
    .enum<ExecutionType> { it.id }
    .default(ExecutionType.VirtualMachine)
  val path by argument("file")

  override fun help(context: Context): String = "Run Program"

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val nativeRegistry = NativeRegistry()
    nativeRegistry.add("internal", InternalNativeProvider(quiet = quiet, debug = debug))
    nativeRegistry.add("java", JavaNativeProvider())
    nativeRegistry.add("ffi", FfiNativeProvider())
    val main = tool.createExecutionContext(executionType, Symbol("main"), ExecutionOptions(
      nativeRegistry = nativeRegistry,
      debug = debug,
    ))
    maybeLoopAndMeasure(loop, measure) {
      main.execute()
    }
  }
}
