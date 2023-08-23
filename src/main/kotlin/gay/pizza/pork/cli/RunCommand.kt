package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.eval.CallableFunction
import gay.pizza.pork.eval.Scope
import gay.pizza.pork.frontend.FileFrontend

class RunCommand : CliktCommand(help = "Run Program", name = "run") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val frontend = FileFrontend(path)
    val scope = Scope()
    scope.define("println", CallableFunction { arguments ->
      for (argument in arguments.values) {
        println(argument)
      }
    })
    frontend.evaluate(scope)
  }
}
