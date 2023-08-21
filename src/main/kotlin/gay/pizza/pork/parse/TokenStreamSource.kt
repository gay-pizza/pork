package gay.pizza.pork.parse

class TokenStreamSource(val stream: TokenStream) : TokenSource {
  private var index = 0
  override val currentIndex: Int = index

  override fun next(): Token {
    if (index == stream.tokens.size) {
      return Token.endOfFile(stream.tokens.size)
    }
    val char = stream.tokens[index]
    index++
    return char
  }

  override fun peek(): Token {
    if (index == stream.tokens.size) {
      return Token.endOfFile(stream.tokens.size)
    }
    return stream.tokens[index]
  }
}
