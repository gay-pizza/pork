package gay.pizza.pork.idea

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

object PorkTokenTypes {
  val Whitespace = TokenType.WHITE_SPACE
  val Keyword = IElementType("keyword", PorkLanguage)
  val Symbol = IElementType("symbol", PorkLanguage)
  val Operator = IElementType("operator", PorkLanguage)
  val String = IElementType("string", PorkLanguage)
  val Number = IElementType("number", PorkLanguage)
}
