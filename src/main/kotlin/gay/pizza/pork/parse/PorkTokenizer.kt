package gay.pizza.pork.parse

class PorkTokenizer(val source: CharSource, val preserveWhitespace: Boolean = false) {
  private var tokenStart: Int = 0

  private fun isSymbol(c: Char): Boolean =
    (c in 'a'..'z') || (c in 'A'..'Z') || c == '_'

  private fun isDigit(c: Char): Boolean =
    c in '0'..'9'

  private fun isWhitespace(c: Char): Boolean =
    c == ' ' || c == '\r' || c == '\n' || c == '\t'

  private fun readSymbolOrKeyword(firstChar: Char): Token {
    val symbol = buildString {
      append(firstChar)
      while (isSymbol(source.peek())) {
        append(source.next())
      }
    }

    var type = TokenType.Symbol
    for (keyword in TokenType.Keywords) {
      if (symbol == keyword.keyword) {
        type = keyword
      }
    }

    return Token(type, symbol)
  }

  private fun readIntLiteral(firstChar: Char): Token {
    val number = buildString {
      append(firstChar)
      while (isDigit(source.peek())) {
        append(source.next())
      }
    }
    return Token(TokenType.IntLiteral, number)
  }

  private fun readWhitespace(): Token {
    val whitespace = buildString {
      while (isWhitespace(source.peek())) {
        val char = source.next()
        append(char)
      }
    }
    return Token(TokenType.Whitespace, whitespace)
  }

  fun next(): Token {
    while (source.peek() != CharSource.NullChar) {
      tokenStart = source.currentIndex
      val char = source.next()

      for (item in TokenType.SingleChars) {
        if (item.char != char) {
          continue
        }

        var type = item
        var text = item.char.toString()
        for (promotion in item.promotions) {
          if (source.peek() != promotion.nextChar) {
            continue
          }
          val nextChar = source.next()
          type = promotion.type
          text += nextChar
        }
        return Token(type, text)
      }

      if (isWhitespace(char)) {
        val whitespace = readWhitespace()
        if (preserveWhitespace) {
          return whitespace
        }
        continue
      }

      if (isDigit(char)) {
        return readIntLiteral(char)
      }

      if (isSymbol(char)) {
        return readSymbolOrKeyword(char)
      }
      throw RuntimeException("Failed to parse: (${char}) next ${source.peek()}")
    }
    return TokenSource.EndOfFile
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
