package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiTreeUtil
import gay.pizza.pork.idea.psi.gen.FunctionDefinitionElement
import gay.pizza.pork.idea.psi.gen.PorkElement

class PorkFunctionReference(element: PorkElement, textRange: TextRange) : PorkReference(element, textRange) {
  override fun resolve(): PorkElement? {
    val functionName = canonicalText
    for (file in getRelevantFiles()) {
      val thisFileFunctionDefinitions = PsiTreeUtil.collectElementsOfType(file, FunctionDefinitionElement::class.java)
      val thisFileFoundFunctionDefinition = thisFileFunctionDefinitions.firstOrNull {
        it.name == functionName
      }
      if (thisFileFoundFunctionDefinition != null) {
        return thisFileFoundFunctionDefinition
      }
    }
    return null
  }
}
