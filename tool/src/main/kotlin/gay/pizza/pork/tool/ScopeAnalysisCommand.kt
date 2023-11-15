package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.frontend.scope.WorldScope
import gay.pizza.pork.minimal.FileTool

class ScopeAnalysisCommand : CliktCommand(help = "Run Scope Analysis", name = "scope-analysis") {
  val path by argument("file")

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val world = tool.buildWorld()
    val root = world.load(tool.rootImportLocator)
    val scope = WorldScope(world).apply { index(root) }
    val rootScope = scope.scope(root)
    for (visibleScopeSymbol in rootScope.internallyVisibleSymbols) {
      println(
        "symbol ${visibleScopeSymbol.scopeSymbol.symbol.id} " +
        "type=${visibleScopeSymbol.scopeSymbol.definition.type.name} " +
        "internal=${visibleScopeSymbol.isInternalSymbol} " +
        "slab=${visibleScopeSymbol.scopeSymbol.slab.location.commonFriendlyName}"
      )
    }
  }
}
