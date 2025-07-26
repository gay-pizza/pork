package gay.pizza.pork.tokenizer

class MatchedCharConsumer(
  val start: CharSequence,
  val end: CharSequence,
  vararg val options: Options
) : CharConsumer {
  private val eofTerminationAllowed = options.contains(Options.AllowEofTermination)

  override fun consume(type: TokenType, tokenizer: Tokenizer): String? {
    if (!tokenizer.peek(start)) {
      return null
    }
    val buffer = StringBuilder()
    tokenizer.read(start.length, buffer)
    var endsNeededToTerminate = 1
    while (true) {
      if (tokenizer.peek(start)) {
        endsNeededToTerminate++
        tokenizer.read(start.length, buffer)
        continue
      }

      if (tokenizer.peek(end)) {
        endsNeededToTerminate--
        tokenizer.read(end.length, buffer)
      }

      if (endsNeededToTerminate == 0) {
        return buffer.toString()
      }

      val char = tokenizer.source.next()
      if (char == CharSource.EndOfFile) {
        if (eofTerminationAllowed) {
          return buffer.toString()
        }
        throw UnterminatedTokenError(type.name, tokenizer.source.currentSourceIndex())
      }
      buffer.append(char)
    }
  }

  enum class Options {
    AllowEofTermination
  }
}
