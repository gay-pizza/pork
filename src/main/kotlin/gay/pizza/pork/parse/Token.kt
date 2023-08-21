package gay.pizza.pork.parse

class Token(val type: TokenType, val start: Int, val text: String) {
  override fun toString(): String = "$start ${type.name} '${text.replace("\n", "\\n")}'"

  companion object {
    fun endOfFile(size: Int): Token =
      Token(TokenType.EndOfFile, size, "")
  }
}
