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
}
