package gay.pizza.pork.idea

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import gay.pizza.pork.parser.Parser

class PorkParser : PsiParser {
  override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
    val psiBuilderMarkAttribution = PsiBuilderMarkAttribution(builder)
    val source = PsiBuilderTokenSource(builder)
    val parser = Parser(source, psiBuilderMarkAttribution)
    try {
      parser.within { parser.readCompilationUnit() }
    } catch (_: ExitParser) {}
    return builder.treeBuilt
  }

  class ExitParser(val error: String) : RuntimeException("Exit Parser: $error")
}
