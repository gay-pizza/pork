package gay.pizza.pork.ast.nodes

import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.NodeVisitor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("importDeclaration")
class ImportDeclaration(val path: StringLiteral) : Declaration() {
  override val type: NodeType = NodeType.ImportDeclaration

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(path)

  override fun equals(other: Any?): Boolean {
    if (other !is ImportDeclaration) return false
    return other.path == path
  }

  override fun hashCode(): Int {
    var result = path.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
