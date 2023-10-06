// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.idea.psi.gen

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import gay.pizza.pork.ast.gen.NodeType
import gay.pizza.pork.idea.psi.PorkElementHelpers
import javax.swing.Icon

class SymbolReferenceElement(node: ASTNode) : PorkNamedElement(node) {
  override fun getName(): String? =
    PorkElementHelpers.nameOfNamedElement(this)

  override fun setName(name: String): PsiElement =
    PorkElementHelpers.setNameOfNamedElement(this, name)

  override fun getNameIdentifier(): PsiElement? =
    PorkElementHelpers.nameIdentifierOfNamedElement(this)

  override fun getReference(): PsiReference? =
    PorkElementHelpers.referenceOfElement(this, NodeType.Node)

  override fun getIcon(flags: Int): Icon? =
    PorkElementHelpers.iconOf(this)

  override fun getPresentation(): ItemPresentation? =
    PorkElementHelpers.presentationOf(this)
}
