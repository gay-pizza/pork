package gay.pizza.pork.tokenizer

interface HighlightScheme {
  fun highlight(token: Token): Highlight
}
