package gay.pizza.pork.idea

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import gay.pizza.pork.parser.TokenFamily
import gay.pizza.pork.parser.TokenType

object PorkSyntaxHighlighter : SyntaxHighlighter {
  override fun getHighlightingLexer(): Lexer {
    return PorkLexer()
  }

  override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
    if (tokenType == null) return emptyArray()
    val ourTokenType = PorkElementTypes.tokenTypeFor(tokenType) ?: return emptyArray()
    val attributes = when (ourTokenType.family) {
      TokenFamily.KeywordFamily ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.KEYWORD",
          DefaultLanguageHighlighterColors.KEYWORD
        )
      TokenFamily.SymbolFamily ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.SYMBOL",
          DefaultLanguageHighlighterColors.LOCAL_VARIABLE
        )
      TokenFamily.OperatorFamily ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.OPERATOR",
          DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
      TokenFamily.StringLiteralFamily ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.STRING",
          DefaultLanguageHighlighterColors.STRING
        )
      TokenFamily.NumericLiteralFamily ->
        TextAttributesKey.createTextAttributesKey(
          "PORK.NUMBER",
          DefaultLanguageHighlighterColors.NUMBER
        )
      TokenFamily.CommentFamily ->
        when (ourTokenType) {
          TokenType.LineComment -> TextAttributesKey.createTextAttributesKey(
            "PORK.COMMENT.LINE",
            DefaultLanguageHighlighterColors.LINE_COMMENT
          )

          else -> TextAttributesKey.createTextAttributesKey(
            "PORK.COMMENT.BLOCK",
            DefaultLanguageHighlighterColors.BLOCK_COMMENT
          )
        }
      else -> null
    }
    return if (attributes == null)
      emptyArray()
    else SyntaxHighlighterBase.pack(attributes)
  }
}
