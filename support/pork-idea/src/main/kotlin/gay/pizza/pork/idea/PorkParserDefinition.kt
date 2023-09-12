package gay.pizza.pork.idea

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class PorkParserDefinition : ParserDefinition {
  val fileElementType = IFileElementType(PorkLanguage)

  override fun createLexer(project: Project?): Lexer {
    return PorkLexer()
  }

  override fun createParser(project: Project?): PsiParser {
    return PorkParser()
  }

  override fun getFileNodeType(): IFileElementType {
    return fileElementType
  }

  override fun getCommentTokens(): TokenSet {
    return PorkElementTypes.CommentSet
  }

  override fun getStringLiteralElements(): TokenSet {
    return PorkElementTypes.StringSet
  }

  override fun createElement(node: ASTNode): PsiElement {
    return ASTWrapperPsiElement(node)
  }

  override fun createFile(viewProvider: FileViewProvider): PsiFile {
    return PorkFile(viewProvider)
  }
}
