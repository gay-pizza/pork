package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.evaluator.*
import gay.pizza.pork.ffi.JavaNativeProvider
import gay.pizza.pork.ffi.JnaNativeProvider

class RunCommand : CliktCommand(help = "Run Program", name = "run") {
  val loop by option("--loop", help = "Loop Program").flag()
  val measure by option("--measure", help = "Measure Time").flag()
  val quiet by option("--quiet", help = "Silence Prints").flag()
  val dumpScope by option("--dump-scope", help = "Dump Scope").flag()
  val path by argument("file")

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val scope = Scope()
    scope.define("println", CallableFunction { arguments ->
      if (quiet) {
        return@CallableFunction None
      }
      for (argument in arguments.values) {
        println(argument)
      }
      None
    })

    val main = tool.loadMainFunction(scope, setupEvaluator = {
      addNativeFunctionProvider("ffi", JnaNativeProvider())
      addNativeFunctionProvider("java", JavaNativeProvider())
    })

    if (dumpScope) {
      val functionContext = main as FunctionContext
      val internalScope = functionContext.compilationUnitContext.internalScope
      internalScope.crawlScopePath { key, path ->
        println("[scope] $key [${path.joinToString(" -> ")}]")
      }
    }

    maybeLoopAndMeasure(loop, measure) {
      main.call(Arguments(emptyList()))
    }
  }
}
