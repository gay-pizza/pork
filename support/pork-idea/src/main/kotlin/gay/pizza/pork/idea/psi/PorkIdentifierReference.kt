package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import gay.pizza.pork.idea.psi.gen.PorkElement

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
}
