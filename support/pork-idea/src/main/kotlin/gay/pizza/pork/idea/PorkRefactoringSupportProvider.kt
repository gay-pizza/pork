package gay.pizza.pork.idea

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import gay.pizza.pork.idea.psi.gen.PorkNamedElement

class PorkRefactoringSupportProvider : RefactoringSupportProvider() {
  override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean {
    return element is PorkNamedElement
  }
}
