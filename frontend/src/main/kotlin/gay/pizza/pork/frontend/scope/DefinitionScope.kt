package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.gen.Definition
import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.ast.gen.visit

class DefinitionScope(val slabScope: SlabScope, val definition: Definition) {
  val usedSymbols: List<ScopeSymbol> by lazy {
    val symbols = mutableListOf<ScopeSymbol>()
    val analyzer = ExternalSymbolUsageAnalyzer()
    analyzer.visit(definition)
    for (symbol in analyzer.usedSymbols) {
      val resolved = slabScope.resolve(symbol)
        ?: throw RuntimeException("Unable to resolve symbol: ${symbol.id}")
      symbols.add(resolved)
    }
    symbols
  }

  fun resolve(symbol: Symbol): ScopeSymbol? = slabScope.resolve(symbol)
}
