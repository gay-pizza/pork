package gay.pizza.pork.idea

import com.intellij.lexer.LexerBase
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.psi.tree.IElementType
import gay.pizza.pork.tokenizer.BadCharacterError
import gay.pizza.pork.tokenizer.StringCharSource
import gay.pizza.pork.tokenizer.Tokenizer
import gay.pizza.pork.tokenizer.UnterminatedTokenError
import com.intellij.psi.TokenType as PsiTokenType

class PorkLexer : LexerBase() {
  private lateinit var source: StringCharSource
  private lateinit var tokenizer: Tokenizer
  private var internalTokenStart: Int = 0
  private var internalTokenEnd: Int = 0
  private var internalState: Int = 0
  private var currentTokenType: IElementType? = null

  override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
    source = StringCharSource(
      input = buffer,
      startIndex = startOffset,
      endIndex = endOffset
    )
    tokenizer = Tokenizer(source)
    internalState = initialState
    internalTokenStart = startOffset
    internalTokenEnd = startOffset
    currentTokenType = null
    advance()
  }

  override fun getState(): Int {
    return internalState
  }

  override fun getTokenType(): IElementType? {
    return currentTokenType
  }

  override fun getTokenStart(): Int {
    return internalTokenStart
  }

  override fun getTokenEnd(): Int {
    return internalTokenEnd
  }

  override fun advance() {
    internalTokenStart = internalTokenEnd
    if (internalTokenStart == bufferEnd) {
      currentTokenType = null
      return
    }

    try {
      val currentToken = tokenizer.next()
      currentTokenType = PorkElementTypes.elementTypeFor(currentToken.type)
      internalTokenStart = currentToken.sourceIndex.index
      internalTokenEnd = currentToken.sourceIndex.index + currentToken.text.length
    } catch (e: ProcessCanceledException) {
      throw e
    } catch (e: BadCharacterError) {
      currentTokenType = PsiTokenType.BAD_CHARACTER
      internalTokenEnd = bufferEnd
    } catch (e: UnterminatedTokenError) {
      currentTokenType = PsiTokenType.BAD_CHARACTER
      internalTokenEnd = bufferEnd
    }
  }

  override fun getBufferSequence(): CharSequence {
    return source.input
  }

  override fun getBufferEnd(): Int {
    return source.endIndex
  }

  override fun toString(): String =
    "PorkLexer(start=$internalTokenStart, end=$internalTokenEnd)"
}
