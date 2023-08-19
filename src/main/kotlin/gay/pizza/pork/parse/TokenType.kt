package gay.pizza.pork.parse

enum class TokenType(val singleChar: Char? = null, val keyword: String? = null) {
  Symbol,
  IntLiteral,
  Equals(singleChar = '='),
  Plus(singleChar = '+'),
  Minus(singleChar = '-'),
  Multiply(singleChar = '*'),
  Divide(singleChar = '/'),
  LeftCurly(singleChar = '{'),
  RightCurly(singleChar = '}'),
  LeftBracket(singleChar = '['),
  RightBracket(singleChar = ']'),
  LeftParentheses(singleChar = '('),
  RightParentheses(singleChar = ')'),
  Comma(singleChar = ','),
  False(keyword = "false"),
  True(keyword = "true"),
  EndOfFile;

  companion object {
    val Keywords = entries.filter { it.keyword != null }
    val SingleChars = entries.filter { it.singleChar != null }
  }
}
