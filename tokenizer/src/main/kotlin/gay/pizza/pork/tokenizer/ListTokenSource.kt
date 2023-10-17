package gay.pizza.pork.tokenizer

class ListTokenSource(val tokens: List<Token>) : TokenSource {
  private var index = 0

  override val currentIndex: Int
    get() = index

  override fun next(): Token {
    if (index == tokens.size) {
      return tokens.last()
    }
    val char = tokens[index]
    index++
    return char
  }

  override fun peek(): Token {
    if (index == tokens.size) {
      return tokens.last()
    }
    return tokens[index]
  }

  override fun peekTypeAhead(ahead: Int): TokenType {
    val calculated = index + ahead
    if (calculated >= tokens.size) {
      return tokens.last().type
    }
    return tokens[calculated].type
  }
}
