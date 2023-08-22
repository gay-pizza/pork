package gay.pizza.pork.parse

class Highlighter(val scheme: HighlightScheme) {
  fun highlight(stream: TokenStream): List<Highlight> =
    stream.tokens.map { scheme.highlight(it) }
}
