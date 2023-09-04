package gay.pizza.pork.parser

class Highlighter(val scheme: HighlightScheme) {
  fun highlight(stream: TokenStream): List<Highlight> =
    stream.tokens.map { scheme.highlight(it) }
}
