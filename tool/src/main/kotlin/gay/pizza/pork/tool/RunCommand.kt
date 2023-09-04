package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.Scope

class RunCommand : CliktCommand(help = "Run Program", name = "run") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
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
