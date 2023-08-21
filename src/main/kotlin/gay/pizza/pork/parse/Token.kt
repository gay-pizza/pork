package gay.pizza.pork.parse

class Token(val type: TokenType, val start: Int, val text: String) {
  override fun toString(): String = "${type.name} $text"

  companion object {
    fun endOfFile(size: Int): Token =
      Token(TokenType.EndOfFile, size, "")
  }
}
