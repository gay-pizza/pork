package gay.pizza.pork.tokenizer

interface CharConsumer {
  fun consume(type: TokenType, tokenizer: Tokenizer): String?
}
