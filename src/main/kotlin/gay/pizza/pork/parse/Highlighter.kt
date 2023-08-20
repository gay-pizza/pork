package gay.pizza.pork.parse

abstract class Highlighter(source: TokenSource) : TokenProcessor(source) {
  override fun process(token: Token) {
    when {
      token.type.keyword != null -> {
        keyword(token)
      }
      token.type == TokenType.Symbol -> {
        symbol(token)
      }
      else -> other(token)
    }
  }

  abstract fun keyword(token: Token)
  abstract fun symbol(token: Token)
  abstract fun other(token: Token)
}