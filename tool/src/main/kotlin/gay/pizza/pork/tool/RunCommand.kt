package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.Scope
import kotlin.system.measureTimeMillis

class RunCommand : CliktCommand(help = "Run Program", name = "run") {
  val loop by option("--loop", help = "Loop Program").flag()
  val measure by option("--measure", help = "Measure Time").flag()
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    if (loop) {
      while (true) {
        runProgramMaybeMeasure()
      }
    } else {
      runProgramMaybeMeasure()
    }
  }

  private fun runProgramMaybeMeasure() {
    if (measure) {
      val time = measureTimeMillis {
        runProgramOnce()
      }
      println("time taken: $time ms")
    } else {
      runProgramOnce()
    }
  }

  private fun runProgramOnce() {
    val tool = FileTool(path)
    val scope = Scope()
    scope.define("println", CallableFunction { arguments ->
      for (argument in arguments.values) {
        println(argument)
      }
    })
    tool.evaluate(scope)
  }
}
