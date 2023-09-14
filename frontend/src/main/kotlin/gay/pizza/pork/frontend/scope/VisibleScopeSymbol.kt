package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.CompilationUnit

class VisibleScopeSymbol(val visibleToUnit: CompilationUnit, val scopeSymbol: ScopeSymbol) {
  val isInternalSymbol: Boolean
    get() = visibleToUnit == scopeSymbol.compilationUnit
}
