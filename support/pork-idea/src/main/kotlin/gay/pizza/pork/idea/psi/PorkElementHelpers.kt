package gay.pizza.pork.idea.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.childrenOfType
import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.idea.PorkElementTypes
import gay.pizza.pork.idea.psi.gen.PorkElement
import gay.pizza.pork.idea.psi.gen.PorkNamedElement
import gay.pizza.pork.idea.psi.gen.SymbolElement

object PorkElementHelpers {
  private val symbolElementType = PorkElementTypes.elementTypeFor(NodeType.Symbol)

  fun nameOfNamedElement(element: PorkNamedElement): String? {
    val child = element.node.findChildByType(symbolElementType)
    return child?.text
  }

  fun setNameOfNamedElement(element: PorkNamedElement, name: String): PsiElement = element

  fun nameIdentifierOfNamedElement(element: PorkNamedElement): PsiElement? {
    val child = element.node.findChildByType(symbolElementType)
    return child?.psi
  }

  fun referenceOfElement(element: PorkElement, type: NodeType): PsiReference? {
    val textRangeOfSymbolInElement = element.childrenOfType<SymbolElement>().firstOrNull()?.textRangeInParent ?: return null
    return PorkIdentifierReference(element, textRangeOfSymbolInElement)
  }
}
