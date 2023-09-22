package gay.pizza.pork.parser

class TokenStreamSource(val stream: TokenStream) : TokenSource {
  private var index = 0

  override val currentIndex: Int
    get() = index

  override fun next(): Token {
    if (index == stream.tokens.size) {
      return stream.tokens.last()
    }
    val char = stream.tokens[index]
    index++
    return char
  }

  override fun peek(): Token {
    if (index == stream.tokens.size) {
      return stream.tokens.last()
    }
    return stream.tokens[index]
  }

  override fun peekTypeAhead(ahead: Int): TokenType {
    val calculated = index + ahead
    if (calculated >= stream.tokens.size) {
      return stream.tokens.last().type
    }
    return stream.tokens[calculated].type
  }
}
