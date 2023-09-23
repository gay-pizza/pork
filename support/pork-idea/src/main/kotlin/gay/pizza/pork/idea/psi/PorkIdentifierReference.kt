package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import gay.pizza.pork.idea.psi.gen.*

class PorkIdentifierReference(element: PorkElement, textRange: TextRange) : PorkReference(element, textRange) {
  override fun resolve(): PorkElement? {
    val identifierName = canonicalText
    val items = findAllCandidates(identifierName)
    if (items.isNotEmpty()) {
      return items.first()
    }
    return null
  }

  override fun getVariants(): Array<Any> {
    val candidates = findAllCandidates()
    return candidates.toTypedArray()
  }

  fun findAllCandidates(name: String? = null): List<PorkElement> =
    listOf(findAnyLocals(name), findAnyDefinitions(name)).flatten()

  fun findAnyLocals(name: String? = null): List<PorkElement> {
    val functionDefinitionElement = PsiTreeUtil.getParentOfType(element, FunctionDefinitionElement::class.java)
      ?: return emptyList()
    val locals = mutableListOf<PorkElement>()

    fun check(localCandidate: PsiElement, upward: Boolean) {
      if (localCandidate is BlockElement && !upward) {
        return
      }

      if (localCandidate is ArgumentSpecElement ||
        localCandidate is LetAssignmentElement ||
        localCandidate is VarAssignmentElement) {
        locals.add(localCandidate as PorkElement)
      }

      if (localCandidate is ForInElement) {
        val forInItem = localCandidate.childrenOfType<ForInItemElement>().firstOrNull()
        if (forInItem != null) {
          locals.add(forInItem)
        }
      }

      localCandidate.children.forEach { check(it, false) }
    }

    PsiTreeUtil.treeWalkUp(element, functionDefinitionElement) { _, localCandidate ->
      if (localCandidate != null)  {
        if (element == functionDefinitionElement) {
          return@treeWalkUp true
        }
        check(localCandidate, true)
      }
      true
    }

    val argumentSpecElements = functionDefinitionElement.childrenOfType<ArgumentSpecElement>()
    locals.addAll(argumentSpecElements)
    val finalLocals = locals.distinctBy { it.textRange }
    return finalLocals.filter { if (name != null) it.name == name else true }
  }

  fun findAnyDefinitions(name: String? = null): List<PorkElement> {
    val foundDefinitions = mutableListOf<PorkNamedElement>()
    for (file in getRelevantFiles()) {
      val definitions = PsiTreeUtil.collectElements(file) { element ->
        element is FunctionDefinitionElement ||
          element is LetDefinitionElement
      }.filterIsInstance<PorkNamedElement>()
      if (name != null) {
        val fileFoundDefinition = definitions.firstOrNull {
          it.name == name
        }

        if (fileFoundDefinition != null) {
          foundDefinitions.add(fileFoundDefinition)
          return foundDefinitions
        }
      } else {
        foundDefinitions.addAll(definitions)
      }
    }
    return foundDefinitions
  }
}
