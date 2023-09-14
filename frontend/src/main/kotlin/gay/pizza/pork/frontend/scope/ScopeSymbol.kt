package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.Definition
import gay.pizza.pork.ast.Node

class ScopeSymbol(
  val compilationUnit: Node,
  val definition: Definition
) {
  val symbol = definition.symbol
}
