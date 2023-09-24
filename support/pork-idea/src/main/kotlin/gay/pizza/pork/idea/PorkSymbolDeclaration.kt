package gay.pizza.pork.idea

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import gay.pizza.pork.idea.psi.PorkElementHelpers
import gay.pizza.pork.idea.psi.gen.PorkElement

@Suppress("UnstableApiUsage")
class PorkSymbolDeclaration(val element: PorkElement) : PsiSymbolDeclaration {
  override fun getDeclaringElement(): PsiElement = element
  override fun getRangeInDeclaringElement(): TextRange {
    val textRangeOfSymbol = PorkElementHelpers.symbolElementOf(element)?.psi?.textRangeInParent
      ?: throw RuntimeException("Unable to get symbol of element: $element")
    return textRangeOfSymbol
  }

  override fun getSymbol(): Symbol = PorkElementHelpers.psiSymbolFor(element) ?:
    throw RuntimeException("Unable to get symbol of element: $element")
}
