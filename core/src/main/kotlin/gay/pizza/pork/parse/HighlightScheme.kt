package gay.pizza.pork.parse

interface HighlightScheme {
  fun highlight(token: Token): Highlight
}
