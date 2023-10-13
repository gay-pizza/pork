// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("importPath")
class ImportPath(val components: List<Symbol>) : Node() {
  override val type: NodeType = NodeType.ImportPath

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(components)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitImportPath(this)

  override fun equals(other: Any?): Boolean {
    if (other !is ImportPath) return false
    return other.components == components
  }

  override fun hashCode(): Int {
    var result = components.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
