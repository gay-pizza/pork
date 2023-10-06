package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.gen.CompilationUnit

class VisibleScopeSymbol(val visibleToUnit: CompilationUnit, val scopeSymbol: ScopeSymbol) {
  val isInternalSymbol: Boolean
    get() = visibleToUnit == scopeSymbol.compilationUnit
}
