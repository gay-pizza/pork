package gay.pizza.pork.tokenizer

class BadCharacterError(char: Char, sourceIndex: SourceIndex, state: TokenizerState) : TokenizeError(
  "Failed to produce token for '${char}' at $sourceIndex in state $state"
)
