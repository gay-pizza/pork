package gay.pizza.pork.parse

import gay.pizza.pork.ast.Node

interface NodeAttribution {
  fun enter()
  fun push(token: Token)
  fun <T: Node> exit(node: T): T
}
