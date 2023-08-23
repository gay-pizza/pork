package gay.pizza.pork.parse

enum class TokenFamily : TokenTypeProperty {
  OperatorFamily,
  KeywordFamily,
  SymbolFamily,
  NumericLiteralFamily,
  StringLiteralFamily,
  CommentFamily,
  OtherFamily
}
