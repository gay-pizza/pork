package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import gay.pizza.pork.frontend.FileFrontend

class ReprintCommand : CliktCommand(help = "Reprint Parsed Compilation Unit", name = "reprint") {
  val path by argument("file").path(mustExist = true, canBeDir = false)

  override fun run() {
    val frontend = FileFrontend(path)
    print(frontend.reprint())
  }
}
