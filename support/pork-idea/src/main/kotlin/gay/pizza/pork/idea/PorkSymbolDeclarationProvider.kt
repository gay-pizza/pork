package gay.pizza.pork.idea

import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.psi.PsiElement
import gay.pizza.pork.idea.psi.gen.FunctionDefinitionElement
import gay.pizza.pork.idea.psi.gen.LetDefinitionElement
import gay.pizza.pork.idea.psi.gen.PorkElement

@Suppress("UnstableApiUsage")
class PorkSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
  override fun getDeclarations(element: PsiElement, offsetInElement: Int): MutableCollection<out PsiSymbolDeclaration> {
    if (element !is PorkElement) return mutableListOf()
    if (element is FunctionDefinitionElement || element is LetDefinitionElement) {
      return mutableListOf(PorkSymbolDeclaration(element))
    }
    return mutableListOf()
  }
}
