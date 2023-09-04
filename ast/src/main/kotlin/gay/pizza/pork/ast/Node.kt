package gay.pizza.pork.ast

import kotlinx.serialization.Serializable

@Serializable
sealed class Node {
  abstract val type: NodeType
  open fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> = emptyList()
}
