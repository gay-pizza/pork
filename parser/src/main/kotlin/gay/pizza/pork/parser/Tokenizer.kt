package gay.pizza.pork.parser

class Tokenizer(val source: CharSource) {
  private var startIndex: SourceIndex = SourceIndex.zero()
  private var currentLineIndex = 1
  private var currentLineColumn = 0

  private fun readBlockComment(firstChar: Char): Token {
    val comment = buildString {
      append(firstChar)
      var endOfComment = false
      while (true) {
        val char = nextChar()
        if (char == CharSource.NullChar) {
          throw UnterminatedTokenError("block comment", currentSourceIndex())
        }
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
    return produceToken(TokenType.BlockComment, comment)
  }

  private fun readLineComment(firstChar: Char): Token {
    val comment = buildString {
      append(firstChar)
      while (true) {
        val char = source.peek()
        if (char == CharSource.NullChar || char == '\n') {
          break
        }
        append(nextChar())
      }
    }
    return produceToken(TokenType.LineComment, comment)
  }

  private fun readStringLiteral(firstChar: Char): Token {
    val string = buildString {
      append(firstChar)
      while (true) {
        val char = source.peek()
        if (char == CharSource.NullChar) {
          throw UnterminatedTokenError("string", currentSourceIndex())
        }
        append(nextChar())
        if (char == '"') {
          break
        }
      }
    }
    return produceToken(TokenType.StringLiteral, string)
  }

  fun next(): Token {
    while (source.peek() != CharSource.NullChar) {
      startIndex = currentSourceIndex()
      val char = nextChar()

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
            val nextChar = nextChar()
            type = promotion.type
            text += nextChar
            promoted = true
          }
        }
        return produceToken(type, text)
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
          throw ParseError("Unknown Char Consumer")
        }

        val text = buildString {
          append(char)

          while (
            if (item.charConsumer != null)
              item.charConsumer.isValid(source.peek())
            else
              item.charIndexConsumer!!.isValid(source.peek(), ++index)
          ) {
            append(nextChar())
          }
        }
        var token = produceToken(item, text)
        val tokenUpgrader = item.tokenUpgrader
        if (tokenUpgrader != null) {
          token = tokenUpgrader.maybeUpgrade(token) ?: token
        }
        return token
      }

      if (char == '"') {
        return readStringLiteral(char)
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

  private fun produceToken(type: TokenType, text: String) =
    Token(type, startIndex, text)

  private fun nextChar(): Char {
    val char = source.next()
    if (char == '\n') {
      currentLineIndex++
      currentLineColumn = 0
    }
    currentLineColumn++
    return char
  }

  private fun currentSourceIndex(): SourceIndex = SourceIndex(source.currentIndex, currentLineIndex, currentLineColumn)
}
