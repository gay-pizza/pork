package gay.pizza.pork.parse

import gay.pizza.pork.parse.TokenTypeProperty.*

enum class TokenType(vararg properties: TokenTypeProperty) {
  Symbol,
  IntLiteral,
  Equality,
  Inequality,
  Equals(SingleChar('='), Promotion('=', Equality)),
  Plus(SingleChar('+')),
  Minus(SingleChar('-')),
  Multiply(SingleChar('*')),
  Divide(SingleChar('/')),
  LeftCurly(SingleChar('{')),
  RightCurly(SingleChar('}')),
  LeftBracket(SingleChar('[')),
  RightBracket(SingleChar(']')),
  LeftParentheses(SingleChar('(')),
  RightParentheses(SingleChar(')')),
  Negation(SingleChar('!'), Promotion('=', Inequality)),
  Comma(SingleChar(',')),
  False(Keyword("false")),
  True(Keyword("true")),
  In(Keyword("in")),
  If(Keyword("if")),
  Then(Keyword("then")),
  Else(Keyword("else")),
  Whitespace,
  BlockComment,
  LineComment,
  EndOfFile;

  val promotions: List<Promotion> = properties.filterIsInstance<Promotion>()
  val keyword: Keyword? = properties.filterIsInstance<Keyword>().singleOrNull()
  val singleChar: SingleChar? = properties.filterIsInstance<SingleChar>().singleOrNull()

  companion object {
    val Keywords = entries.filter { item -> item.keyword != null }
    val SingleChars = entries.filter { item -> item.singleChar != null }
  }
}
