package gay.pizza.pork.parser

interface HighlightScheme {
  fun highlight(token: Token): Highlight
}
