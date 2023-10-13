package gay.pizza.pork.idea

import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.ParameterInfoHandler
import com.intellij.lang.parameterInfo.ParameterInfoUIContext
import com.intellij.lang.parameterInfo.ParameterInfoUtils
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.openapi.util.TextRange
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.elementsAtOffsetUp
import gay.pizza.pork.idea.psi.gen.ArgumentSpecElement
import gay.pizza.pork.idea.psi.gen.FunctionCallElement
import gay.pizza.pork.idea.psi.gen.FunctionDefinitionElement
import gay.pizza.pork.parser.TokenType

@Suppress("UnstableApiUsage")
class PorkParameterInfoHandler : ParameterInfoHandler<FunctionCallElement, FunctionDefinitionElement> {
  override fun findElementForParameterInfo(context: CreateParameterInfoContext): FunctionCallElement? {
    return context.file.elementsAtOffsetUp(context.offset).asSequence()
      .map { it.first }
      .filterIsInstance<FunctionCallElement>()
      .firstOrNull()
  }

  override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): FunctionCallElement? {
    return context.file.elementsAtOffsetUp(context.offset).asSequence()
      .map { it.first }
      .filterIsInstance<FunctionCallElement>()
      .firstOrNull()
  }

  override fun updateUI(p: FunctionDefinitionElement, context: ParameterInfoUIContext) {
    val argumentSpecs = p.childrenOfType<ArgumentSpecElement>()
    val signature = argumentSpecs.mapNotNull { it.name }.joinToString(", ")
    if (argumentSpecs.isEmpty()) {
      context.setupUIComponentPresentation(
        "<no parameters>",
        -1,
        -1,
        false,
        false,
        false,
        context.defaultParameterColor
      )
      return
    }
    if (context.currentParameterIndex >= argumentSpecs.size) {
      context.setupUIComponentPresentation(
        signature,
        -1,
        -1,
        false,
        false,
        false,
        context.defaultParameterColor
      )
    } else {
      var range: TextRange? = null
      var start = 0
      for ((index, item) in signature.split(", ").withIndex()) {
        if (index == context.currentParameterIndex) {
          range = TextRange(index, index + item.length)
        }
        start += item.length + 2
      }
      context.setupUIComponentPresentation(
        signature,
        range?.startOffset ?: 0,
        range?.endOffset ?: (signature.length - 1),
        false,
        false,
        false,
        context.defaultParameterColor
      )
    }
  }

  override fun updateParameterInfo(parameterOwner: FunctionCallElement, context: UpdateParameterInfoContext) {
    val offset = ParameterInfoUtils.getCurrentParameterIndex(
      parameterOwner.node,
      context.offset,
      PorkElementTypes.elementTypeFor(TokenType.Comma)
    )
    context.setCurrentParameter(offset)
  }

  override fun showParameterInfo(element: FunctionCallElement, context: CreateParameterInfoContext) {
    context.showHint(element, element.textOffset, this)
  }
}
