package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.frontend.Slab

class SlabScope(val worldScope: WorldScope, val slab: Slab) {
  private val externalSymbolsList = mutableSetOf<ScopeSymbol>()
  private val internalSymbolsList = mutableSetOf<ScopeSymbol>()

  val externalSymbols: Set<ScopeSymbol>
    get() = externalSymbolsList

  val internalSymbols: Set<ScopeSymbol>
    get() = internalSymbolsList

  val internallyVisibleSymbols: List<VisibleScopeSymbol> by lazy { findInternallyVisibleSymbols() }

  fun index() {
    for (definition in slab.compilationUnit.definitions) {
      val scopeSymbol = ScopeSymbol(this, definition)
      if (definition.modifiers.export) {
        externalSymbolsList.add(scopeSymbol)
      }
      internalSymbolsList.add(scopeSymbol)
    }
  }

  private fun findInternallyVisibleSymbols(): List<VisibleScopeSymbol> {
    val allSymbols = mutableMapOf<Symbol, VisibleScopeSymbol>()
    val imports = slab.importedSlabs
    for (import in imports) {
      val scope = worldScope.index(import)
      for (importedSymbol in scope.externalSymbols) {
        allSymbols[importedSymbol.symbol] = VisibleScopeSymbol(slab, importedSymbol)
      }
    }

    for (internalSymbol in internalSymbols) {
      allSymbols[internalSymbol.symbol] = VisibleScopeSymbol(slab, internalSymbol)
    }

    return allSymbols.values.toList()
  }

  fun resolve(symbol: Symbol): ScopeSymbol? = internallyVisibleSymbols.firstOrNull {
    it.scopeSymbol.symbol == symbol
  }?.scopeSymbol
}
