package gay.pizza.pork.tokenizer

interface TokenTypeProperty {
  class SingleChar(val char: Char) : TokenTypeProperty
  class Promotion(val nextChar: Char, val type: TokenType) : TokenTypeProperty
  class ManyChars(val text: String) : TokenTypeProperty
  class AnyOf(vararg val strings: String): TokenTypeProperty
  class InsideStates(vararg val states: TokenizerState) : TokenTypeProperty
  open class CharMatch(val matcher: CharMatcher) : TokenTypeProperty
  open class CharConsume(val consumer: CharConsumer) : TokenTypeProperty
  open class TokenUpgrader(val maybeUpgrade: (Token) -> Token?) : TokenTypeProperty

  object KeywordUpgrader : TokenUpgrader({ token ->
    var upgraded: Token? = null
    for (item in TokenType.ManyChars) {
      if (item.manyChars != null && token.text == item.manyChars.text) {
        upgraded = token.upgrade(item)
        break
      }
    }

    if (upgraded == null) {
      for (item in TokenType.AnyOf) {
        if (item.anyOf != null && item.anyOf.strings.contains(token.text)) {
          upgraded = token.upgrade(item)
          break
        }
      }
    }
    upgraded
  })
}
