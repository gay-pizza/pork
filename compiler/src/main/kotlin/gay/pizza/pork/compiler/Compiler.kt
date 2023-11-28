package gay.pizza.pork.compiler

import gay.pizza.pork.bir.IrSlab
import gay.pizza.pork.bir.IrSymbolAssignment
import gay.pizza.pork.bir.IrWorld
import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.bytecode.MutableConstantPool
import gay.pizza.pork.frontend.Slab
import gay.pizza.pork.frontend.World
import gay.pizza.pork.frontend.scope.ScopeSymbol

class Compiler(val world: World) {
  val constantPool: MutableConstantPool = MutableConstantPool()
  val compilableSlabs: ComputableState<Slab, CompilableSlab> = ComputableState { slab ->
    CompilableSlab(this, slab)
  }

  val irSymbolAssignment: IrSymbolAssignment = IrSymbolAssignment()
  val irSymbolWorld: IrSymbolWorld<Any> = IrSymbolWorld(irSymbolAssignment)

  fun resolveOrNull(scopeSymbol: ScopeSymbol): CompilableSymbol? {
    val compiledSlab = compilableSlabs.of(scopeSymbol.slabScope.slab)
    return compiledSlab.resolve(scopeSymbol.symbol)
  }

  fun resolve(scopeSymbol: ScopeSymbol): CompilableSymbol = resolveOrNull(scopeSymbol) ?:
    throw RuntimeException(
      "Unable to resolve scope symbol: " +
      "${scopeSymbol.slabScope.slab.location.commonLocationIdentity} ${scopeSymbol.symbol.id}")

  fun contributeCompiledSymbols(
    into: MutableSet<CompilableSymbol>,
    symbol: ScopeSymbol,
    resolved: CompilableSymbol = resolve(symbol)
  ) {
    if (!into.add(resolved)) {
      return
    }

    for (used in resolved.usedSymbols) {
      contributeCompiledSymbols(into, used)
    }
  }

  fun compileIrWorld(): IrWorld {
    val slabs = mutableListOf<IrSlab>()
    for (slab in world.slabs) {
      slabs.add(compilableSlabs.of(slab).compiledIrSlab)
    }
    return IrWorld(slabs)
  }

  fun compile(entryPointSymbol: CompilableSymbol): CompiledWorld {
    val usedSymbolSet = mutableSetOf<CompilableSymbol>()
    contributeCompiledSymbols(usedSymbolSet, entryPointSymbol.scopeSymbol, entryPointSymbol)
    val layout = CompiledWorldLayout(this)
    for (used in usedSymbolSet) {
      layout.add(used)
    }
    return layout.build()
  }
}
