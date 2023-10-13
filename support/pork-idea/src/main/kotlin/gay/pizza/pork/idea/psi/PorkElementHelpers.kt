package gay.pizza.pork.idea.psi

import com.intellij.lang.ASTNode
import com.intellij.model.Symbol
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.util.PlatformIcons
import gay.pizza.pork.ast.gen.NodeType
import gay.pizza.pork.common.unused
import gay.pizza.pork.idea.PorkDeclarationSymbol
import gay.pizza.pork.idea.PorkElementTypes
import gay.pizza.pork.idea.PorkLanguage
import gay.pizza.pork.idea.psi.gen.*
import javax.swing.Icon

@Suppress("UnstableApiUsage")
object PorkElementHelpers {
  private val symbolElementType = PorkElementTypes.elementTypeFor(NodeType.Symbol)

  fun nameOfNamedElement(element: PorkNamedElement): String? {
    val child = symbolElementOf(element) ?: return null
    return child.text
  }

  fun setNameOfNamedElement(element: PorkNamedElement, name: String): PsiElement {
    val child = symbolElementOf(element) ?: return element
    val factory = PsiFileFactory.getInstance(element.project) as PsiFileFactoryImpl
    val created = factory.createElementFromText(name, PorkLanguage, child.elementType, element.context) as PorkElement
    element.node.replaceChild(child, created.node)
    return element
  }

  fun symbolElementOf(element: PorkElement): ASTNode? {
    var child = element.node.findChildByType(symbolElementType)
    if (child == null) {
      child = PsiTreeUtil.collectElementsOfType(element, SymbolElement::class.java).firstOrNull()?.node
    }
    return child
  }

  fun nameIdentifierOfNamedElement(element: PorkNamedElement): PsiElement? {
    return symbolElementOf(element)?.psi
  }

  fun referenceOfElement(element: PorkElement, type: NodeType): PsiReference? {
    unused(type)

    if (element is ImportPathElement) {
      return PorkFileReference(element, element.textRange)
    }

    val symbols = element.childrenOfType<SymbolElement>()
    val textRangeOfSymbolInElement = symbols.firstOrNull()?.textRangeInParent ?: return null
    return PorkIdentifierReference(element, textRangeOfSymbolInElement)
  }

  fun iconOf(element: PorkElement): Icon? {
    return when (element) {
      is LetDefinitionElement -> PlatformIcons.FIELD_ICON
      is FunctionDefinitionElement -> PlatformIcons.FUNCTION_ICON
      is LetAssignmentElement -> PlatformIcons.VARIABLE_READ_ACCESS
      is VarAssignmentElement -> PlatformIcons.VARIABLE_RW_ACCESS
      is ArgumentSpecElement -> PlatformIcons.VARIABLE_READ_ACCESS
      else -> null
    }
  }

  fun presentationOf(element: PorkElement): ItemPresentation? {
    val icon = iconOf(element)
    if (element is FunctionDefinitionElement || element is LetDefinitionElement) {
      return PorkPresentable(element.name, icon, element.containingFile.virtualFile?.name)
    }

    if (element is LetAssignmentElement || element is VarAssignmentElement) {
      return PorkPresentable(element.name, icon)
    }

    return null
  }

  fun psiSymbolFor(element: PorkElement): Symbol? {
    val symbolElement = symbolElementOf(element) ?: return null
    val module = element.containingFile?.virtualFile?.path ?: element.containingFile?.name ?: return null
    if (element is FunctionDefinitionElement || element is LetDefinitionElement) {
      return PorkDeclarationSymbol(module, symbolElement.text)
    }
    return null
  }
}
