package gay.pizza.pork.tokenizer

class Token(val type: TokenType, val sourceIndex: SourceIndex, val text: String) {
  override fun toString(): String =
    "$sourceIndex ${type.name} '${text.replace("\n", "\\n")}'"

  companion object {
    fun endOfFile(sourceIndex: SourceIndex): Token =
      Token(TokenType.EndOfFile, sourceIndex, "")
  }

  fun upgrade(upgradedType: TokenType): Token = Token(upgradedType, sourceIndex, text)
}
