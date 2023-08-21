package gay.pizza.pork.parse

class TokenStream(val tokens: List<Token>) {
  fun excludeAllWhitespace(): TokenStream =
    TokenStream(tokens.filter { it.type != TokenType.Whitespace })

  override fun toString(): String = tokens.toString()
}
