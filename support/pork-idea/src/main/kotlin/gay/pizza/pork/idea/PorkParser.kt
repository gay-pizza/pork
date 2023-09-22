package gay.pizza.pork.idea

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.impl.PsiElementBase
import com.intellij.psi.tree.IElementType
import gay.pizza.pork.parser.Parser

class PorkParser : PsiParser {
  override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
    val marker = builder.mark()
    val psiBuilderMarkAttribution = PsiBuilderMarkAttribution(builder)
    val source = PsiBuilderTokenSource(builder)
    val parser = Parser(source, psiBuilderMarkAttribution)
    try {
      parser.parseCompilationUnit()
    } catch (_: ExitParser) {}
    marker.done(root)
    return builder.treeBuilt
  }

  class ExitParser(error: String? = null) : RuntimeException(
    if (error == null) "Fast Exit" else "Exit Parser: $error"
  )
}
