package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.gen.Definition
import gay.pizza.pork.ast.gen.Node

class ScopeSymbol(
  val compilationUnit: Node,
  val definition: Definition
) {
  val symbol = definition.symbol
}
