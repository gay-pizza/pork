package gay.pizza.pork.parse

class TokenStreamSource(val stream: TokenStream) : TokenSource {
  private var index = 0
  override val currentIndex: Int = index

  override fun next(): Token {
    if (index == stream.tokens.size) {
      return TokenSource.EndOfFile
    }
    val char = stream.tokens[index]
    index++
    return char
  }

  override fun peek(): Token {
    if (index == stream.tokens.size) {
      return TokenSource.EndOfFile
    }
    return stream.tokens[index]
  }
}
