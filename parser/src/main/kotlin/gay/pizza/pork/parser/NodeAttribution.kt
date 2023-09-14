package gay.pizza.pork.parser

import gay.pizza.pork.ast.Node
import gay.pizza.pork.ast.NodeType

interface NodeAttribution {
  fun push(token: Token)
  fun <T: Node> adopt(node: T)
  fun <T: Node> guarded(type: NodeType?, block: () -> T): T
}
