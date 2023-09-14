package gay.pizza.pork.tool

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
      ReprintCommand(),
      ParseCommand(),
      AstCommand(),
      AttributeCommand(),
      ScopeAnalysisCommand()
    )
  }

  override fun run() {}
}
