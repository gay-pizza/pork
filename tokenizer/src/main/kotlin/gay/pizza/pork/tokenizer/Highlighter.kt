package gay.pizza.pork.tokenizer

class Highlighter(val scheme: HighlightScheme) {
  fun highlight(stream: TokenStream): List<Highlight> =
    stream.tokens.map { scheme.highlight(it) }
}
