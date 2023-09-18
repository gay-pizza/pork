package gay.pizza.pork.idea

import com.intellij.lang.PsiBuilder
import gay.pizza.pork.parser.SourceIndex
import gay.pizza.pork.parser.Token
import gay.pizza.pork.parser.TokenSource
import com.intellij.psi.TokenType as PsiTokenType

@Suppress("UnstableApiUsage")
class PsiBuilderTokenSource(val builder: PsiBuilder) : TokenSource {
  override val currentIndex: Int = 0

  override fun next(): Token {
    val token = peek()
    builder.advanceLexer()
    return token
  }

  override fun peek(): Token {
    if (builder.eof()) {
      return Token.endOfFile(SourceIndex.indexOnly(builder.currentOffset))
    }
    val elementType = builder.tokenType!!
    if (elementType == PsiTokenType.BAD_CHARACTER) {
      throw BadCharacterError("Invalid character")
    }
    val tokenType = PorkElementTypes.tokenTypeFor(elementType) ?:
      throw RuntimeException("Lexing failure: ${elementType.debugName}")
    return Token(tokenType, SourceIndex.indexOnly(builder.currentOffset), builder.tokenText!!)
  }

  class BadCharacterError(error: String) : RuntimeException(error)
}
