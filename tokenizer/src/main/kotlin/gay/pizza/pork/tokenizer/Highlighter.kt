package gay.pizza.pork.tokenizer

class Highlighter(val scheme: HighlightScheme) {
  fun highlight(source: TokenSource): Sequence<Highlight> =
    source.sequence().map { scheme.highlight(it) }
}
