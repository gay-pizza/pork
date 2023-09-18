package gay.pizza.pork.parser

class UnterminatedTokenError(what: String, sourceIndex: SourceIndex) : ParseError(
  "Unterminated $what at $sourceIndex"
)
