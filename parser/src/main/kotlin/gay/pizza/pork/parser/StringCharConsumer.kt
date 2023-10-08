package gay.pizza.pork.parser

object StringCharConsumer : CharConsumer {
  override fun consume(type: TokenType, tokenizer: Tokenizer): String? {
    if (!tokenizer.peek("\"")) {
      return null
    }

    val buffer = StringBuilder()
    buffer.append(tokenizer.source.next())
    var escape = false
    while (true) {
      val char = tokenizer.source.peek()

      if (char == CharSource.EndOfFile) {
        throw UnterminatedTokenError("String", tokenizer.source.currentSourceIndex())
      }

      buffer.append(tokenizer.source.next())

      if (char == '\\') {
        escape = true
      } else if (char == '"' && !escape) {
        break
      }
    }
    return buffer.toString()
  }
}
