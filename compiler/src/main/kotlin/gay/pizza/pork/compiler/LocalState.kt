package gay.pizza.pork.compiler

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.bytecode.MutableRel

class LocalState(val symbol: CompilableSymbol) {
  private var internalLoopState: LoopState? = null
  val loopState: LoopState?
    get() = internalLoopState

  private var localVarIndex: UInt = 0u
  private val variables = mutableListOf<MutableList<StubVar>>()

  fun startLoop(startOfLoop: UInt, exitJumpTarget: MutableRel) {
    internalLoopState = LoopState(
      startOfLoop = startOfLoop,
      exitJumpTarget = exitJumpTarget,
      scopeDepth = (internalLoopState?.scopeDepth ?: 0u) + 1u,
      enclosing = internalLoopState
    )
  }

  fun endLoop() {
    internalLoopState = internalLoopState?.enclosing
  }

  fun createLocal(symbol: Symbol): StubVar {
    val scope = variables.last()
    val variable = StubVar(localVarIndex++, symbol)
    scope.add(variable)
    return variable
  }

  fun createAnonymousLocal(): StubVar {
    val scope = variables.last()
    val variable = StubVar(localVarIndex++)
    scope.add(variable)
    return variable
  }

  fun pushScope() {
    variables.add(mutableListOf())
  }

  fun popScope() {
    variables.removeLast()
  }

  fun resolve(symbol: Symbol): Loadable {
    for (scope in variables.reversed()) {
      val found = scope.firstOrNull { it.symbol == symbol }
      if (found != null) {
        return Loadable(stubVar = found)
      }
    }
    val found = this.symbol.compilableSlab.resolveVisible(symbol)
    if (found != null) {
      return Loadable(call = found)
    }
    throw RuntimeException("Unable to resolve symbol: ${symbol.id}")
  }
}
