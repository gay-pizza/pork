package gay.pizza.pork.parser

class LazyTokenSource(val tokenizer: Tokenizer) : TokenSource {
  private val queue = mutableListOf<Token>()
  private var index = 0
  override val currentIndex: Int
    get() = index

  override fun next(): Token {
    index++
    if (queue.isNotEmpty()) {
      return queue.removeFirst()
    }
    return tokenizer.next()
  }

  override fun peek(): Token {
    if (queue.isNotEmpty()) {
      return queue.first()
    }
    val token = tokenizer.next()
    queue.add(token)
    return token
  }

  override fun peekTypeAhead(ahead: Int): TokenType {
    wantAtLeast(ahead + 1)
    return queue[ahead].type
  }

  private fun wantAtLeast(ahead: Int) {
    if (queue.size < ahead) {
      for (i in 1..ahead) {
        queue.add(tokenizer.next())
      }
    }
  }
}
