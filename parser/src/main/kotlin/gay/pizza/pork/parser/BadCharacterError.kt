package gay.pizza.pork.parser

class BadCharacterError(val char: Char, sourceIndex: SourceIndex, state: TokenizerState) : ParseError(
  "Failed to produce token for '${char}' at $sourceIndex in state $state"
)
