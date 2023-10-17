package gay.pizza.pork.tokenizer

class ExpectedTokenError(got: Token, sourceIndex: SourceIndex, vararg expectedTypes: TokenType) : TokenizeError(
  message(got, sourceIndex, expectedTypes)
) {
  companion object {
    fun message(got: Token, sourceIndex: SourceIndex, expectedTypes: Array<out TokenType>): String {
      val tokenTypeMessages = expectedTypes.map {
        if (it.simpleWantString != null)
          "${it.name} '${it.simpleWantString}'"
        else
          it.name
      }

      val expected = if (expectedTypes.size > 1) {
        "one of " + tokenTypeMessages.joinToString(", ")
      } else tokenTypeMessages.firstOrNull() ?: "unknown"

      val friendlyIndex = if (sourceIndex.locationReliable) {
        "line ${sourceIndex.line} column ${sourceIndex.column}"
      } else {
        "index ${sourceIndex.index}"
      }

      return "Expected $expected at $friendlyIndex but got ${got.type} '${got.text}'"
    }
  }
}
