package gay.pizza.pork.idea

import com.intellij.lexer.LexerBase
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.psi.tree.IElementType
import gay.pizza.pork.parser.*
import com.intellij.psi.TokenType as PsiTokenType

class PorkLexer : LexerBase() {
  private val log: Logger = Logger.getInstance(PorkLexer::class.java)

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
      currentTokenType = tokenAsElement(currentToken)
      internalTokenStart = currentToken.start
      internalTokenEnd = currentToken.start + currentToken.text.length
    } catch (e: ProcessCanceledException) {
      throw e
    } catch (e: Throwable) {
      currentTokenType = PsiTokenType.BAD_CHARACTER
      internalTokenEnd = bufferEnd
      log.warn(Tokenizer::class.java.name, e)
    }
  }

  override fun getBufferSequence(): CharSequence {
    return source.input
  }

  override fun getBufferEnd(): Int {
    return source.endIndex
  }

  private fun tokenAsElement(token: Token): IElementType = when  {
    token.type.family == TokenFamily.KeywordFamily ->
      PorkTokenTypes.Keyword
    token.type.family == TokenFamily.SymbolFamily ->
      PorkTokenTypes.Symbol
    token.type.family == TokenFamily.OperatorFamily ->
      PorkTokenTypes.Operator
    token.type.family == TokenFamily.StringLiteralFamily ->
      PorkTokenTypes.String
    token.type.family == TokenFamily.NumericLiteralFamily ->
      PorkTokenTypes.Number
    token.type == TokenType.Whitespace ->
      PorkTokenTypes.Whitespace
    token.type == TokenType.BlockComment ->
      PorkTokenTypes.BlockComment
    token.type == TokenType.LineComment ->
      PorkTokenTypes.LineComment
    else -> PsiTokenType.CODE_FRAGMENT
  }

  override fun toString(): String =
    "PorkLexer(start=$internalTokenStart, end=$internalTokenEnd)"
}
