// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("compilationUnit")
class CompilationUnit(val declarations: List<Declaration>, val definitions: List<Definition>) : Node() {
  override val type: NodeType = NodeType.CompilationUnit

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(declarations, definitions)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitCompilationUnit(this)

  override fun equals(other: Any?): Boolean {
    if (other !is CompilationUnit) return false
    return other.declarations == declarations && other.definitions == definitions
  }

  override fun hashCode(): Int {
    var result = declarations.hashCode()
    result = 31 * result + definitions.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
