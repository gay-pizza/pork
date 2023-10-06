// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("importDeclaration")
class ImportDeclaration(val form: Symbol, val components: List<Symbol>) : Declaration() {
  override val type: NodeType = NodeType.ImportDeclaration

  override fun <T> visitChildren(visitor: NodeVisitor<T>): List<T> =
    visitor.visitAll(listOf(form), components)

  override fun <T> visit(visitor: NodeVisitor<T>): T =
    visitor.visitImportDeclaration(this)

  override fun equals(other: Any?): Boolean {
    if (other !is ImportDeclaration) return false
    return other.form == form && other.components == components
  }

  override fun hashCode(): Int {
    var result = form.hashCode()
    result = 31 * result + components.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
