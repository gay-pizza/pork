package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import gay.pizza.pork.idea.psi.gen.PorkElement

abstract class PorkReference(element: PorkElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange), PorkReferencable {
  override val internalPorkElement: PorkElement = element
}
