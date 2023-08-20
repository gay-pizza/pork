package gay.pizza.pork.parse

abstract class TokenProcessor(val source: TokenSource) {
  fun processAll() {
    while (true) {
      val token = source.next()
      process(token)
      if (token.type == TokenType.EndOfFile) {
        break
      }
    }
  }

  abstract fun process(token: Token)
}
