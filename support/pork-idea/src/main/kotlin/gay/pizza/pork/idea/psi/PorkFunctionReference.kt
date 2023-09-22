package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.PsiTreeUtil
import gay.pizza.pork.idea.psi.gen.FunctionDefinitionElement
import gay.pizza.pork.idea.psi.gen.PorkElement

class PorkFunctionReference(element: PorkElement, textRange: TextRange) : PorkReference(element, textRange) {
  override fun resolve(): PorkElement? {
    val functionName = canonicalText
    val functionDefinitions = findFunctionDefinitions(functionName)
    if (functionDefinitions.isNotEmpty()) {
      return functionDefinitions.first()
    }
    return null
  }

  override fun getVariants(): Array<Any> {
    return findFunctionDefinitions().toTypedArray()
  }

  fun findFunctionDefinitions(name: String? = null): List<FunctionDefinitionElement> {
    val foundFunctionDefinitions = mutableListOf<FunctionDefinitionElement>()
    for (file in getRelevantFiles()) {
      val fileFunctionDefinitions = PsiTreeUtil.collectElementsOfType(file, FunctionDefinitionElement::class.java)
      if (name != null) {
        val fileFoundDefinition = fileFunctionDefinitions.firstOrNull {
          it.name == name
        }

        if (fileFoundDefinition != null) {
          foundFunctionDefinitions.add(fileFoundDefinition)
          return foundFunctionDefinitions
        }
      } else {
        foundFunctionDefinitions.addAll(fileFunctionDefinitions)
      }
    }
    return foundFunctionDefinitions
  }
}
