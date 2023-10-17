package gay.pizza.pork.tokenizer

enum class TokenFamily : TokenTypeProperty {
  OperatorFamily,
  KeywordFamily,
  SymbolFamily,
  NumericLiteralFamily,
  StringLiteralFamily,
  CommentFamily,
  OtherFamily
}
