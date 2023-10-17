package gay.pizza.pork.parser

import gay.pizza.pork.ast.gen.Node
import gay.pizza.pork.ast.gen.NodeType
import gay.pizza.pork.tokenizer.Token

interface NodeAttribution {
  fun push(token: Token)
  fun <T: Node> adopt(node: T)
  fun <T: Node> produce(type: NodeType, block: () -> T): T
}
