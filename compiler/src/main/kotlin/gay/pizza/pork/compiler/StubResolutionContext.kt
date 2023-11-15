package gay.pizza.pork.compiler

interface StubResolutionContext {
  fun resolveJumpTarget(symbol: CompilableSymbol): UInt
}
