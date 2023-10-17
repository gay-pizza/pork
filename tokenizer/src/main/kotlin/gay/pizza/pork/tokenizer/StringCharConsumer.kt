package gay.pizza.pork.tokenizer

object StringCharConsumer : CharConsumer {
  override fun consume(type: TokenType, tokenizer: Tokenizer): String {
    val buffer = StringBuilder()
    var escape = false
    while (true) {
      val char = tokenizer.source.peek()

      if (char == CharSource.EndOfFile) {
        throw UnterminatedTokenError("String", tokenizer.source.currentSourceIndex())
      }

      if (char == '"' && !escape) {
        break
      }

      if (escape) {
        escape = false
      }

      buffer.append(tokenizer.source.next())

      if (char == '\\') {
        escape = true
      }
    }
    return buffer.toString()
  }
}
