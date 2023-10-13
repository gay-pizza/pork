package gay.pizza.pork.parser

class Tokenizer(source: CharSource) {
  val source: SourceIndexCharSource = SourceIndexCharSource(source)
  private var startIndex: SourceIndex = SourceIndex.zero()
  private var state = TokenizerState.Normal

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

  fun next(): Token {
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

  fun stream(): TokenStream {
    val tokens = mutableListOf<Token>()
    while (true) {
      val token = next()
      tokens.add(token)
      if (token.type == TokenType.EndOfFile) {
        break
      }
    }
    return TokenStream(tokens)
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
