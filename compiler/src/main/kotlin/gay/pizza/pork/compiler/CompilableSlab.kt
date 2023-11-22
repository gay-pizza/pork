package gay.pizza.pork.compiler

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.frontend.Slab

class CompilableSlab(val compiler: Compiler, val slab: Slab) {
  val compilableSymbols: List<CompilableSymbol> by lazy {
    slab.scope.internalSymbols.map { symbol ->
      CompilableSymbol(this, symbol)
    }
  }

  fun resolve(symbol: Symbol): CompilableSymbol? = compilableSymbols.firstOrNull {
    it.scopeSymbol.symbol == symbol
  }

  fun resolveVisible(symbol: Symbol): CompilableSymbol? {
    val scopeSymbol = slab.scope.resolve(symbol) ?: return null
    return compiler.resolveOrNull(scopeSymbol)
  }
}
