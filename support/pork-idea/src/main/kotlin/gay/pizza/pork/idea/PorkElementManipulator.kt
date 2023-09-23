package gay.pizza.pork.idea

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.elementType
import gay.pizza.pork.idea.psi.gen.PorkElement

class PorkElementManipulator : AbstractElementManipulator<PorkElement>() {
  override fun handleContentChange(element: PorkElement, range: TextRange, newContent: String): PorkElement? {
    val sourceText = element.text
    val beforeText = sourceText.substring(0, range.startOffset)
    val afterText = sourceText.substring(range.endOffset)
    val changedText = beforeText + newContent + afterText
    return element.replace(produce(element, changedText)) as PorkElement?
  }

  fun produce(element: PorkElement, changed: String): PorkElement {
    val factory = PsiFileFactory.getInstance(element.project) as PsiFileFactoryImpl
    return factory.createElementFromText(changed, PorkLanguage, element.elementType!!, element.context) as PorkElement
  }
}
