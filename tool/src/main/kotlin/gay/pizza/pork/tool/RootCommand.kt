package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands

class RootCommand : CliktCommand("pork") {
  init {
    subcommands(
      RunCommand(),
      HighlightCommand(),
      TokenizeCommand(),
      ReprintCommand(),
      ParseCommand(),
      AstCommand(),
      AttributeCommand(),
      ScopeAnalysisCommand(),
      CopyStdlibCommand(),
      CompileCommand(),
    )
  }

  override fun help(context: Context): String = "Pork - The BBQ Language"

  override fun run() {}
}
