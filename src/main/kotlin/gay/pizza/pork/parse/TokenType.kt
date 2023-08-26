package gay.pizza.pork.parse

import gay.pizza.pork.parse.TokenTypeProperty.*
import gay.pizza.pork.parse.TokenFamily.*

enum class TokenType(vararg properties: TokenTypeProperty) {
  Symbol(SymbolFamily, CharConsumer { (it in 'a'..'z') || (it in 'A'..'Z') || it == '_' }, KeywordUpgrader),
  IntLiteral(NumericLiteralFamily, CharConsumer { it in '0'..'9' }),
  StringLiteral(StringLiteralFamily),
  Equality(OperatorFamily),
  Inequality(OperatorFamily),
  Equals(SingleChar('='), Promotion('=', Equality)),
  Plus(SingleChar('+'), OperatorFamily),
  Minus(SingleChar('-'), OperatorFamily),
  Multiply(SingleChar('*'), OperatorFamily),
  Divide(SingleChar('/'), OperatorFamily),
  LeftCurly(SingleChar('{')),
  RightCurly(SingleChar('}')),
  LeftBracket(SingleChar('[')),
  RightBracket(SingleChar(']')),
  LeftParentheses(SingleChar('(')),
  RightParentheses(SingleChar(')')),
  Negation(SingleChar('!'), Promotion('=', Inequality), OperatorFamily),
  Comma(SingleChar(',')),
  False(Keyword("false"), KeywordFamily),
  True(Keyword("true"), KeywordFamily),
  In(Keyword("in"), KeywordFamily),
  If(Keyword("if"), KeywordFamily),
  Then(Keyword("then"), KeywordFamily),
  Else(Keyword("else"), KeywordFamily),
  Whitespace(CharConsumer { it == ' ' || it == '\r' || it == '\n' || it == '\t' }),
  BlockComment(CommentFamily),
  LineComment(CommentFamily),
  EndOfFile;

  val promotions: List<Promotion> = properties.filterIsInstance<Promotion>()
  val keyword: Keyword? = properties.filterIsInstance<Keyword>().singleOrNull()
  val singleChar: SingleChar? = properties.filterIsInstance<SingleChar>().singleOrNull()
  val family: TokenFamily =
    properties.filterIsInstance<TokenFamily>().singleOrNull() ?: OtherFamily
  val charConsumer: CharConsumer? = properties.filterIsInstance<CharConsumer>().singleOrNull()
  val tokenUpgrader: TokenUpgrader? = properties.filterIsInstance<TokenUpgrader>().singleOrNull()

  companion object {
    val Keywords = entries.filter { item -> item.keyword != null }
    val SingleChars = entries.filter { item -> item.singleChar != null }
    val CharConsumers = entries.filter { item -> item.charConsumer != null }
  }
}
