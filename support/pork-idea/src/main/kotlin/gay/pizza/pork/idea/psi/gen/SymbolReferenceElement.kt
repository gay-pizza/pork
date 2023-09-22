// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.idea.psi.gen

import com.intellij.psi.PsiReference
import com.intellij.lang.ASTNode
import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.idea.psi.PorkElementHelpers

class SymbolReferenceElement(node: ASTNode) : PorkElement(node) {
  override fun getReference(): PsiReference? =
    PorkElementHelpers.referenceOfElement(this, NodeType.Node)
}
