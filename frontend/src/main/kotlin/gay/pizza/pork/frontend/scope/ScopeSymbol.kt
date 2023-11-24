package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.gen.Definition
import gay.pizza.pork.ast.gen.Symbol

class ScopeSymbol(val slabScope: SlabScope, val definition: Definition) {
  val symbol: Symbol = definition.symbol
  val scope: DefinitionScope by lazy { DefinitionScope(slabScope, definition) }

  override fun equals(other: Any?): Boolean {
    if (other !is ScopeSymbol) return false
    return other.slabScope.slab == slabScope.slab && other.symbol == symbol
  }

  override fun hashCode(): Int {
    var result = slabScope.hashCode()
    result = 31 * result + definition.hashCode()
    result = 31 * result + symbol.hashCode()
    return result
  }

  override fun toString(): String = "ScopeSymbol(${symbol.id})"
}
