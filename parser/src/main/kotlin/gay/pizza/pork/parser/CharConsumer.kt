package gay.pizza.pork.parser

interface CharConsumer {
  fun consume(type: TokenType, tokenizer: Tokenizer): String?
}
