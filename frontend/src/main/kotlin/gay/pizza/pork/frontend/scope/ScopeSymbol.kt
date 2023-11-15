package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.gen.Definition
import gay.pizza.pork.ast.gen.Symbol

class ScopeSymbol(val slabScope: SlabScope, val definition: Definition) {
  val symbol: Symbol = definition.symbol
  val scope: DefinitionScope by lazy { DefinitionScope(slabScope, definition) }
}
