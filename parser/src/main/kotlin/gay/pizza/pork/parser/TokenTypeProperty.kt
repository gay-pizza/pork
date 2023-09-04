package gay.pizza.pork.parser

interface TokenTypeProperty {
  class SingleChar(val char: Char) : TokenTypeProperty
  class Promotion(val nextChar: Char, val type: TokenType) : TokenTypeProperty
  class Keyword(val text: String) : TokenTypeProperty
  class CharConsumer(val isValid: (Char) -> Boolean) : TokenTypeProperty
  open class TokenUpgrader(val maybeUpgrade: (Token) -> Token?) : TokenTypeProperty

  object KeywordUpgrader : TokenUpgrader({ token ->
    var upgraded: Token? = null
    for (item in TokenType.Keywords) {
      if (item.keyword != null && token.text == item.keyword.text) {
        upgraded = Token(item, token.start, token.text)
        break
      }
    }
    upgraded
  })
}
