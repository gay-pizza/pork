package gay.pizza.pork.parser

interface TokenSource : PeekableSource<Token> {
  fun peekTypeAhead(ahead: Int): TokenType
  fun consumeAllRemainingTokens(): List<Token> {
    val tokens = mutableListOf<Token>()
    while (true) {
      val token = next()
      tokens.add(token)
      if (token.type == TokenType.EndOfFile) {
        break
      }
    }
    return tokens
  }

  fun streamAllRemainingTokens(): TokenStream =
    TokenStream(consumeAllRemainingTokens().filter { !TokenType.ParserIgnoredTypes.contains(it.type) })

  fun ignoringParserIgnoredTypes(): TokenSource =
    TokenStreamSource(streamAllRemainingTokens())
}
