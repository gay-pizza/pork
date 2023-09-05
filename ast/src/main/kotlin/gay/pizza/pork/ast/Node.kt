package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("node")
sealed class Node {
  abstract val type: NodeType

  open fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    emptyList()
}
