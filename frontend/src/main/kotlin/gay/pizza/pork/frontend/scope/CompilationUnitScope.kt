package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.gen.CompilationUnit
import gay.pizza.pork.ast.gen.Symbol

class CompilationUnitScope(val worldScope: WorldScope, val unit: CompilationUnit) {
  val externalSymbols = mutableSetOf<ScopeSymbol>()
  val internalSymbols = mutableSetOf<ScopeSymbol>()

  fun index() {
    for (definition in unit.definitions) {
      val scopeSymbol = ScopeSymbol(unit, definition)
      if (definition.modifiers.export) {
        externalSymbols.add(scopeSymbol)
      }
      internalSymbols.add(scopeSymbol)
    }
  }

  fun findInternallyVisibleSymbols(): Set<VisibleScopeSymbol> {
    val allSymbols = mutableMapOf<Symbol, VisibleScopeSymbol>()
    val imports = worldScope.world.importedBy(unit)
    for (import in imports) {
      val scope = worldScope.index(import)
      for (importedSymbol in scope.externalSymbols) {
        allSymbols[importedSymbol.symbol] = VisibleScopeSymbol(unit, importedSymbol)
      }
    }

    for (internalSymbol in internalSymbols) {
      allSymbols[internalSymbol.symbol] = VisibleScopeSymbol(unit, internalSymbol)
    }

    return allSymbols.values.toSet()
  }
}
