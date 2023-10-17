package gay.pizza.pork.tokenizer

class Tokenizer(source: CharSource) : TokenSource {
  internal val source = SourceIndexCharSource(source)

  private var startIndex: SourceIndex = SourceIndex.zero()
  private var state = TokenizerState.Normal
  private var index = 0
  override val currentIndex: Int
    get() = index

  private val queue = mutableListOf<Token>()

  override fun next(): Token {
    val token = readNextToken()
    index++
    return token
  }

  override fun peek(): Token {
    if (queue.isEmpty()) {
      val token = readNextToken()
      queue.add(token)
      return token
    }
    return queue.first()
  }

  override fun peekTypeAhead(ahead: Int): TokenType {
    val needed = ahead - (queue.size - 1)
    if (needed > 0) {
      for (i in 1..needed) {
        queue.add(readNextToken())
      }
    }
    return queue[ahead].type
  }

  private fun nextTokenOrNull(): Token? {
    if (source.peek() == CharSource.EndOfFile) {
      source.next()
      return Token.endOfFile(source.currentSourceIndex())
    }

    startIndex = source.currentSourceIndex()

    for (item in TokenType.CharConsumes) {
      if (!item.validStates.contains(state)) {
        continue
      }
      val text = item.charConsume!!.consumer.consume(item, this)
      if (text != null) {
        return produceToken(item, text)
      }
    }

    val char = source.next()

    for (item in TokenType.SingleChars) {
      if (!item.validStates.contains(state)) {
        continue
      }

      val itemChar = item.singleChar!!.char
      if (itemChar != char) {
        continue
      }

      var type = item
      var text = itemChar.toString()
      var promoted = true
      while (promoted) {
        promoted = false
        for (promotion in type.promotions) {
          if (source.peek() != promotion.nextChar) {
            continue
          }
          val nextChar = source.next()
          type = promotion.type
          text += nextChar
          promoted = true
        }
      }
      return produceToken(type, text)
    }

    var index = 0
    for (item in TokenType.CharMatches) {
      if (!item.validStates.contains(state)) {
        continue
      }

      if (!item.charMatch!!.matcher.valid(char, index)) {
        continue
      }

      val text = buildString {
        append(char)

        while (item.charMatch.matcher.valid(source.peek(), ++index)) {
          append(source.next())
        }
      }
      var token = produceToken(item, text)
      val tokenUpgrader = item.tokenUpgrader
      if (tokenUpgrader != null) {
        token = tokenUpgrader.maybeUpgrade(token) ?: token
      }
      return token
    }
    return null
  }

  private fun readNextToken(): Token {
    val what = source.peek()
    val token = nextTokenOrNull()
    if (token != null) {
      for (transition in state.transitions) {
        if (transition.produced == token.type) {
          state = transition.enter
          break
        }
      }
      return token
    }
    throw BadCharacterError(what, source.currentSourceIndex(), state)
  }

  internal fun produceToken(type: TokenType, text: String) =
    Token(type, startIndex, text)

  internal fun peek(what: CharSequence): Boolean {
    var current = 0
    for (c in what) {
      if (source.peek(current) != c) {
        return false
      }
      current++
    }
    return true
  }

  internal fun read(count: Int, buffer: StringBuilder) {
    for (i in 1..count) {
      buffer.append(source.next())
    }
  }
}
