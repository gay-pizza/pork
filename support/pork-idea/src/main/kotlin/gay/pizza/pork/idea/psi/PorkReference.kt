package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import gay.pizza.pork.idea.psi.gen.ImportDeclarationElement
import gay.pizza.pork.idea.psi.gen.PorkElement
import gay.pizza.pork.idea.psi.gen.SymbolElement

abstract class PorkReference(element: PorkElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange), PorkReferencable {
  override val internalPorkElement: PorkElement = element
}
