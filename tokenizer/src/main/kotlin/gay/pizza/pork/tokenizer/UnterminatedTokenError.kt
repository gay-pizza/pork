package gay.pizza.pork.tokenizer

class UnterminatedTokenError(what: String, sourceIndex: SourceIndex) : TokenizeError(
  "Unterminated $what at $sourceIndex"
)
