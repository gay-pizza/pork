// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.idea.psi.gen

import com.intellij.psi.PsiElement
import com.intellij.lang.ASTNode
import gay.pizza.pork.idea.psi.PorkElementHelpers

class SetAssignmentElement(node: ASTNode) : PorkNamedElement(node) {
  override fun getName(): String? =
    PorkElementHelpers.nameOfNamedElement(this)

  override fun setName(name: String): PsiElement =
    PorkElementHelpers.setNameOfNamedElement(this, name)

  override fun getNameIdentifier(): PsiElement? =
    PorkElementHelpers.nameIdentifierOfNamedElement(this)
}
