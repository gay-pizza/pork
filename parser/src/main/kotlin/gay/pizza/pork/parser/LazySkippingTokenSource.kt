package gay.pizza.pork.parser

import gay.pizza.pork.tokenizer.Token
import gay.pizza.pork.tokenizer.TokenSource
import gay.pizza.pork.tokenizer.TokenType

class LazySkippingTokenSource(val source: TokenSource, val skipping: Set<TokenType>) : ParserAwareTokenSource {
  private var index = 0
  override val currentIndex: Int
    get() = index

  private val queue = mutableListOf<Token>()

  override fun next(): Token {
    needs(1)
    return queue.removeFirst()
  }

  override fun peek(): Token {
    needs(1)
    return queue.first()
  }

  override fun peekTypeAhead(ahead: Int): TokenType {
    needs(ahead + 1)
    return queue[ahead].type
  }

  private fun needs(count: Int) {
    while (queue.size < count) {
      val token = source.next()
      if (!skipping.contains(token.type)) {
        queue.add(token)
      }
    }
  }
}
