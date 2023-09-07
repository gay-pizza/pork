// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("native")
class Native(val form: Symbol, val definition: StringLiteral) : Node() {
  override val type: NodeType = NodeType.Native

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(form, definition)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitNative(this)

  override fun equals(other: Any?): Boolean {
    if (other !is Native) return false
    return other.form == form && other.definition == definition
  }

  override fun hashCode(): Int {
    var result = form.hashCode()
    result = 31 * result + definition.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
