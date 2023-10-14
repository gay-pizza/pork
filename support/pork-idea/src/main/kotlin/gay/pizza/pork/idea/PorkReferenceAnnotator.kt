package gay.pizza.pork.idea

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import gay.pizza.pork.idea.psi.gen.PorkElement

class PorkReferenceAnnotator : Annotator {
  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    if (element !is PorkElement) {
      return
    }

    val reference = element.reference ?: return
    val resolved = reference.resolve()
    if (resolved != null) {
      return
    }

    holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved reference")
      .range(element.textRange)
      .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
      .create()
  }
}
