package gay.pizza.pork.compiler

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.bir.IrDefinition
import gay.pizza.pork.bir.IrSlab
import gay.pizza.pork.bir.IrSlabLocation
import gay.pizza.pork.frontend.Slab

class CompilableSlab(val compiler: Compiler, val slab: Slab) {
  val compiledIrSlab: IrSlab by lazy { compileIrSlab() }

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

  private fun compileIrSlab(): IrSlab {
    val definitions = mutableListOf<IrDefinition>()
    for (compilableSymbol in compilableSymbols) {
      definitions.add(compilableSymbol.compiledIrDefinition)
    }
    val irSlabLocation = IrSlabLocation(slab.location.form, slab.location.filePath)
    return IrSlab(irSlabLocation, definitions)
  }
}
