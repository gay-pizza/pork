package gay.pizza.pork.idea

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

object PorkSyntaxHighlighter : SyntaxHighlighter {
  override fun getHighlightingLexer(): Lexer {
    return PorkLexer()
  }

  override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
    if (tokenType == null) return emptyArray()
    val attributes = when (tokenType) {
      PorkTokenTypes.Keyword ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.KEYWORD",
          DefaultLanguageHighlighterColors.KEYWORD
        )
      PorkTokenTypes.Symbol ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.SYMBOL",
          DefaultLanguageHighlighterColors.LOCAL_VARIABLE
        )
      PorkTokenTypes.Operator ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.OPERATOR",
          DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
      PorkTokenTypes.String ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.STRING",
          DefaultLanguageHighlighterColors.STRING
        )
      PorkTokenTypes.Number ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.NUMBER",
          DefaultLanguageHighlighterColors.NUMBER
        )
      else -> null
    }
    return if (attributes == null)
      emptyArray()
    else SyntaxHighlighterBase.pack(attributes)
  }
}
