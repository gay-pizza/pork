package gay.pizza.pork.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class RootCommand : CliktCommand(
  help = "Pork - The BBQ Language",
  name = "pork"
) {
  init {
    subcommands(
      RunCommand(),
      HighlightCommand(),
      TokenizeCommand(),
      GenerateKotlinCommand()
    )
  }

  override fun run() {}
}
