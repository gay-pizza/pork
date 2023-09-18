package gay.pizza.pork.parser

class BadCharacterError(val char: Char, sourceIndex: SourceIndex) : ParseError(
  "Failed to produce token for '${char}' at $sourceIndex"
)
