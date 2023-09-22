// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("native")
class Native(val form: Symbol, val definitions: List<StringLiteral>) : Node() {
  override val type: NodeType = NodeType.Native

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(listOf(form), definitions)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitNative(this)

  override fun equals(other: Any?): Boolean {
    if (other !is Native) return false
    return other.form == form && other.definitions == definitions
  }

  override fun hashCode(): Int {
    var result = form.hashCode()
    result = 31 * result + definitions.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
