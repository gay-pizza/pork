package gay.pizza.pork.parser

open class AnsiHighlightScheme : HighlightScheme {
  override fun highlight(token: Token): Highlight {
    val attributes = when (token.type.family) {
      TokenFamily.StringLiteralFamily -> string()
      TokenFamily.OperatorFamily -> operator()
      TokenFamily.KeywordFamily -> keyword()
      TokenFamily.SymbolFamily -> symbol()
      TokenFamily.CommentFamily -> comment()
      else -> null
    }

    return if (attributes != null) {
      Highlight(token, ansi(attributes, token.text))
    } else Highlight(token)
  }

  open fun string(): AnsiAttributes =
    AnsiAttributes("32m")
  open fun symbol(): AnsiAttributes =
    AnsiAttributes("33m")
  open fun operator(): AnsiAttributes =
    AnsiAttributes("34m")
  open fun keyword(): AnsiAttributes =
    AnsiAttributes("35m")
  open fun comment(): AnsiAttributes =
    AnsiAttributes("37m")

  private fun ansi(attributes: AnsiAttributes, text: String): String =
    "\u001b[${attributes.color}${text}\u001b[0m"

  class AnsiAttributes(
    val color: String
  )
}
