// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("nativeFunctionDescriptor")
class NativeFunctionDescriptor(val form: Symbol, val definitions: List<StringLiteral>) : Node() {
  override val type: NodeType = NodeType.NativeFunctionDescriptor

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(listOf(form), definitions)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitNativeFunctionDescriptor(this)

  override fun equals(other: Any?): Boolean {
    if (other !is NativeFunctionDescriptor) return false
    return other.form == form && other.definitions == definitions
  }

  override fun hashCode(): Int {
    var result = form.hashCode()
    result = 31 * result + definitions.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
