package gay.pizza.pork.tokenizer

enum class TokenizerState(vararg val transitions: Transition) {
  Normal(Transition({ TokenType.Quote }) { StringLiteralStart }),
  StringLiteralStart(Transition({ TokenType.StringLiteral }) { StringLiteralEnd }),
  StringLiteralEnd(Transition({ TokenType.Quote }) { Normal });

  data class Transition(private val producedToken: () -> TokenType, private val nextState: () -> TokenizerState) {
    val produced by lazy { producedToken() }
    val enter by lazy { nextState() }
  }
}
