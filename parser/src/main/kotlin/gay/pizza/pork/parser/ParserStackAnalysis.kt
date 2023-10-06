package gay.pizza.pork.parser

import gay.pizza.pork.ast.gen.NodeType

class ParserStackAnalysis(private val stack: Array<StackTraceElement>) {
  constructor(throwable: Throwable) : this(throwable.stackTrace)

  fun findDescentPath(): List<NodeType> {
    val parseDescentPaths = mutableListOf<NodeType>()
    for (element in stack) {
      if (element.className != Parser::class.java.name) {
        continue
      }

      if (!element.methodName.startsWith("parse")) {
        continue
      }

      val nodeTypeString = element.methodName.substring(5)
      val type = NodeType.entries.firstOrNull { it.name == nodeTypeString }
      if (type != null) {
        parseDescentPaths.add(type)
      }
    }
    return parseDescentPaths.reversed()
  }
}
