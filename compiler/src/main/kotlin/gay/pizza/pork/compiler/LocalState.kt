package gay.pizza.pork.compiler

import gay.pizza.pork.bir.IrSymbol
import gay.pizza.pork.bytecode.MutableRel
import gay.pizza.pork.frontend.scope.ScopeSymbol

class LocalState(val symbol: CompilableSymbol) {
  private var internalLoopState: LoopState? = null

  private var localVarIndex: UInt = 0u
  private val stubVariables = mutableMapOf<IrSymbol, StubVar>()
  private val loops = mutableMapOf<IrSymbol, LoopState>()

  fun startLoop(symbol: IrSymbol, startOfLoop: UInt, exitJumpTarget: MutableRel) {
    val existing = loops[symbol]
    if (existing != null) {
      throw CompileError("Starting loop that already is started")
    }
    val loopState = LoopState(
      startOfLoop = startOfLoop,
      exitJumpTarget = exitJumpTarget,
      scopeDepth = (internalLoopState?.scopeDepth ?: 0u) + 1u
    )
    loops[symbol] = loopState
  }

  fun findLoopState(symbol: IrSymbol): LoopState =
    loops[symbol] ?: throw CompileError("Unable to find target loop")

  fun endLoop(symbol: IrSymbol) {
    loops.remove(symbol) ?: throw CompileError("End of loop target not found")
  }

  fun createOrFindLocal(symbol: IrSymbol): StubVar {
    val existing = stubVariables[symbol]
    if (existing != null) {
      return existing
    }
    val variable = StubVar(localVarIndex++, symbol.id)
    stubVariables[symbol] = variable
    return variable
  }

  fun resolve(symbol: IrSymbol): Loadable {
    val localStubVar = stubVariables[symbol]
    if (localStubVar != null) {
      return Loadable(stubVar = localStubVar)
    }
    val value = this.symbol.compilableSlab.compiler.irSymbolWorld.resolve(symbol) ?:
      throw RuntimeException("Unable to resolve symbol: ${symbol.id} ${symbol.tag}")
    val scopeSymbol = value as ScopeSymbol
    val call = this.symbol.compilableSlab.compiler.resolve(scopeSymbol)
    return Loadable(call = call)
  }
}
