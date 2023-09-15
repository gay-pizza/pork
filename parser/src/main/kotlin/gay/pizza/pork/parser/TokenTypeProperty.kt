package gay.pizza.pork.parser

interface TokenTypeProperty {
  class SingleChar(val char: Char) : TokenTypeProperty
  class Promotion(val nextChar: Char, val type: TokenType) : TokenTypeProperty
  class ManyChars(val text: String) : TokenTypeProperty
  class AnyOf(vararg val strings: String): TokenTypeProperty
  class CharConsumer(val isValid: (Char) -> Boolean) : TokenTypeProperty
  class CharIndexConsumer(val isValid: (Char, Int) -> Boolean) : TokenTypeProperty
  open class TokenUpgrader(val maybeUpgrade: (Token) -> Token?) : TokenTypeProperty

  object KeywordUpgrader : TokenUpgrader({ token ->
    var upgraded: Token? = null
    for (item in TokenType.ManyChars) {
      if (item.manyChars != null && token.text == item.manyChars.text) {
        upgraded = Token(item, token.start, token.text)
        break
      }
    }
    if (upgraded == null) {
      for(item in TokenType.AnyOf) {
        if(item.anyOf != null && item.anyOf.strings.contains(token.text)) {
          upgraded = Token(item, token.start, token.text)
          break
        }
      }
    }
    upgraded
  })
}
