package gay.pizza.pork.parser

class Tokenizer(val source: CharSource) {
  private var tokenStart: Int = 0

  private fun readBlockComment(firstChar: Char): Token {
    val comment = buildString {
      append(firstChar)
      var endOfComment = false
      while (true) {
        val char = source.next()
        append(char)

        if (endOfComment) {
          if (char != '/') {
            endOfComment = false
            continue
          }
          break
        }

        if (char == '*') {
          endOfComment = true
        }
      }
    }
    return Token(TokenType.BlockComment, tokenStart, comment)
  }

  private fun readLineComment(firstChar: Char): Token {
    val comment = buildString {
      append(firstChar)
      while (true) {
        val char = source.peek()
        if (char == CharSource.NullChar || char == '\n') {
          break
        }
        append(source.next())
      }
    }
    return Token(TokenType.LineComment, tokenStart, comment)
  }

  private fun readStringLiteral(firstChar: Char): Token {
    val string = buildString {
      append(firstChar)
      while (true) {
        val char = source.peek()
        if (char == CharSource.NullChar) {
          throw RuntimeException("Unterminated string.")
        }
        append(source.next())
        if (char == '"') {
          break
        }
      }
    }
    return Token(TokenType.StringLiteral, tokenStart, string)
  }

  fun next(): Token {
    while (source.peek() != CharSource.NullChar) {
      tokenStart = source.currentIndex
      val char = source.next()

      if (char == '/' && source.peek() == '*') {
        return readBlockComment(char)
      }

      if (char == '/' && source.peek() == '/') {
        return readLineComment(char)
      }

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
        return Token(type, tokenStart, text)
      }

      var index = 0
      for (item in TokenType.CharConsumers) {
        if (item.charConsumer != null) {
          if (!item.charConsumer.isValid(char)) {
            continue
          }
        } else if (item.charIndexConsumer != null) {
          if (!item.charIndexConsumer.isValid(char, index)) {
            continue
          }
        } else {
          throw RuntimeException("Unknown Char Consumer")
        }

        val text = buildString {
          append(char)

          while (
            if (item.charConsumer != null)
              item.charConsumer.isValid(source.peek())
            else
              item.charIndexConsumer!!.isValid(source.peek(), ++index)
          ) {
            append(source.next())
          }
        }
        var token = Token(item, tokenStart, text)
        val tokenUpgrader = item.tokenUpgrader
        if (tokenUpgrader != null) {
          token = tokenUpgrader.maybeUpgrade(token) ?: token
        }
        return token
      }

      if (char == '"') {
        return readStringLiteral(char)
      }

      throw RuntimeException("Failed to parse: (${char}) next ${source.peek()}")
    }
    return Token.endOfFile(source.currentIndex)
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
}
