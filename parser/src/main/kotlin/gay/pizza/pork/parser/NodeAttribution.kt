package gay.pizza.pork.parser

import gay.pizza.pork.ast.Node

interface NodeAttribution {
  fun enter()
  fun push(token: Token)
  fun <T: Node> adopt(node: T)
  fun <T: Node> exit(node: T): T
}
