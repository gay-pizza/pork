package gay.pizza.pork.idea.psi

import gay.pizza.pork.ast.Node
import gay.pizza.pork.idea.PorkNodeKey
import gay.pizza.pork.idea.psi.gen.PorkElement

val PorkElement.porkNode: Node?
  get() = node.getUserData(PorkNodeKey)

val PorkElement.porkNodeChecked: Node
  get() = porkNode!!
