package gay.pizza.pork.parse

interface TokenSource : PeekableSource<Token> {
  companion object {
    val EndOfFile = Token(TokenType.EndOfFile, "")
  }
}
