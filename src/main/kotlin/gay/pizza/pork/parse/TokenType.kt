package gay.pizza.pork.parse

enum class TokenType(val char: Char? = null, val keyword: String? = null) {
  Symbol,
  IntLiteral,
  Equals(char = '='),
  Plus(char = '+'),
  Minus(char = '-'),
  Multiply(char = '*'),
  Divide(char = '/'),
  LeftCurly(char = '{'),
  RightCurly(char = '}'),
  LeftBracket(char = '['),
  RightBracket(char = ']'),
  LeftParentheses(char = '('),
  RightParentheses(char = ')'),
  Comma(char = ','),
  False(keyword = "false"),
  True(keyword = "true"),
  In(keyword = "in"),
  If(keyword = "if"),
  Then(keyword = "then"),
  Else(keyword = "else"),
  EndOfFile;

  companion object {
    val Keywords = entries.filter { it.keyword != null }
    val SingleChars = entries.filter { it.char != null }
  }
}
