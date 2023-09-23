package gay.pizza.pork.idea

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.ast.parse
import gay.pizza.pork.parser.Parser

class PorkParser : PsiParser {
  override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
    val nodeTypeForParse = if (root is IFileElementType) {
      NodeType.CompilationUnit
    } else {
      PorkElementTypes.nodeTypeFor(root) ?:
        throw RuntimeException("Unable to parse element type: $root")
    }
    val marker = builder.mark()
    val psiBuilderMarkAttribution = PsiBuilderMarkAttribution(builder)
    val source = PsiBuilderTokenSource(builder)
    val parser = Parser(source, psiBuilderMarkAttribution)
    try {
      parser.parse(nodeTypeForParse)
    } catch (_: ExitParser) {}
    marker.done(root)
    return builder.treeBuilt
  }

  class ExitParser(error: String? = null) : RuntimeException(
    if (error == null) "Fast Exit" else "Exit Parser: $error"
  )
}
