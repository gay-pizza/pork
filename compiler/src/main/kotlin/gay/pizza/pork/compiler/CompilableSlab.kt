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
}
