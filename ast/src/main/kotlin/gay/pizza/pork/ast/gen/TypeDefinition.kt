// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("typeDefinition")
class TypeDefinition(override val modifiers: DefinitionModifiers, override val symbol: Symbol, val nativeTypeDescriptor: NativeTypeDescriptor?) : Definition() {
  override val type: NodeType = NodeType.TypeDefinition

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitNodes(symbol, nativeTypeDescriptor)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitTypeDefinition(this)

  override fun equals(other: Any?): Boolean {
    if (other !is TypeDefinition) return false
    return other.modifiers == modifiers && other.symbol == symbol && other.nativeTypeDescriptor == nativeTypeDescriptor
  }

  override fun hashCode(): Int {
    var result = modifiers.hashCode()
    result = 31 * result + symbol.hashCode()
    result = 31 * result + nativeTypeDescriptor.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
