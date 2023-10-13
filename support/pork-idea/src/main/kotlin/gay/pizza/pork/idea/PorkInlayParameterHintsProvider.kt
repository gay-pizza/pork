package gay.pizza.pork.idea

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import gay.pizza.pork.idea.psi.gen.ArgumentSpecElement
import gay.pizza.pork.idea.psi.gen.FunctionDefinitionElement

@Suppress("UnstableApiUsage")
class PorkInlayParameterHintsProvider : InlayParameterHintsProvider {
  override fun getParameterHints(element: PsiElement): MutableList<InlayInfo> {
    val inlays = mutableListOf<InlayInfo>()
    val resolved = element.reference?.resolve()
    if (resolved !is FunctionDefinitionElement) {
      return inlays
    }
    val argumentSpecs = resolved.childrenOfType<ArgumentSpecElement>()
    val arguments = if (element.children.isNotEmpty()) {
      element.children.drop(1)
    } else emptyList()

    for ((argument, spec) in arguments.zip(argumentSpecs)) {
      val name = spec.name
      if (name != null) {
        inlays.add(InlayInfo(name, argument.textOffset))
      }
    }
    return inlays
  }

  override fun getDefaultBlackList(): MutableSet<String> =
    mutableSetOf()
}
