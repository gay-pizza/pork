package gay.pizza.pork.parse

interface TokenTypeProperty {
  class SingleChar(val char: Char) : TokenTypeProperty
  class Promotion(val nextChar: Char, val type: TokenType) : TokenTypeProperty
  class Keyword(val text: String) : TokenTypeProperty
}
