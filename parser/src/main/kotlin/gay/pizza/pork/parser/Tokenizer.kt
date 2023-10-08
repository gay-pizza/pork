package gay.pizza.pork.parser

class Tokenizer(source: CharSource) {
  val source: SourceIndexCharSource = SourceIndexCharSource(source)

  private var startIndex: SourceIndex = SourceIndex.zero()

  fun next(): Token {
    while (source.peek() != CharSource.EndOfFile) {
      startIndex = source.currentSourceIndex()

      for (item in TokenType.CharConsumes) {
        val text = item.charConsume!!.consumer.consume(item, this)
        if (text != null) {
          return produceToken(item, text)
        }
      }

      val char = source.next()

      for (item in TokenType.SingleChars) {
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

      throw BadCharacterError(char, startIndex)
    }
    return Token.endOfFile(startIndex.copy(index = source.currentIndex))
  }

  fun tokenize(): TokenStream {
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
