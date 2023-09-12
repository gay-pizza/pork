package gay.pizza.pork.idea

import com.intellij.model.psi.PsiSymbolDeclaration
import com.intellij.model.psi.PsiSymbolDeclarationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import gay.pizza.pork.ast.NodeType

@Suppress("UnstableApiUsage")
class PorkSymbolDeclarationProvider : PsiSymbolDeclarationProvider {
  override fun getDeclarations(
    element: PsiElement,
    offsetInElement: Int
  ): MutableCollection<out PsiSymbolDeclaration> {
    val symbolDeclarations = mutableListOf<PsiSymbolDeclaration>()
    if (element.elementType == PorkElementTypes.elementTypeFor(NodeType.FunctionDefinition)) {
      symbolDeclarations.add(PorkSymbolDeclaration(element))
    }
    return symbolDeclarations
  }
}
