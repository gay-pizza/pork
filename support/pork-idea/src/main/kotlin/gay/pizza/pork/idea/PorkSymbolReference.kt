package gay.pizza.pork.idea

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import gay.pizza.pork.idea.psi.PorkElementHelpers
import gay.pizza.pork.idea.psi.PorkReferencable
import gay.pizza.pork.idea.psi.gen.PorkElement

@Suppress("UnstableApiUsage")
class PorkSymbolReference(override val internalPorkElement: PorkElement, val range: TextRange) : PsiSymbolReference, PorkReferencable {
  override fun getElement(): PsiElement = internalPorkElement
  override fun getRangeInElement(): TextRange = range
  override fun resolveReference(): MutableCollection<out Symbol> {
    return findAllCandidates().mapNotNull { PorkElementHelpers.psiSymbolFor(it) }.toMutableList()
  }
}
