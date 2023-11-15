package gay.pizza.pork.frontend.scope

import gay.pizza.pork.frontend.Slab

class VisibleScopeSymbol(val visibleToSlab: Slab, val scopeSymbol: ScopeSymbol) {
  val isInternalSymbol: Boolean
    get() = visibleToSlab == scopeSymbol.slabScope.slab
}
