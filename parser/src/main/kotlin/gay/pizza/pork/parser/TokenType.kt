package gay.pizza.pork.parser

import gay.pizza.pork.parser.TokenTypeProperty.*
import gay.pizza.pork.parser.TokenFamily.*

enum class TokenType(vararg properties: TokenTypeProperty) {
  NumberLiteral(NumericLiteralFamily, CharIndexConsumer { it, index ->
    (it in '0'..'9') || (index > 0 && it == '.') }),
  Symbol(SymbolFamily, CharConsumer {
    (it in 'a'..'z') ||
      (it in 'A'..'Z') ||
      (it == '_') ||
      (it in '0' .. '9')}, KeywordUpgrader),
  StringLiteral(StringLiteralFamily),
  Equality(OperatorFamily),
  Inequality(ManyChars("!="), OperatorFamily),
  ExclaimationPoint(SingleChar('!'), Promotion('=', Inequality)),
  Equals(SingleChar('='), Promotion('=', Equality)),
  PlusPlus(ManyChars("++"), OperatorFamily),
  MinusMinus(ManyChars("--"), OperatorFamily),
  Plus(SingleChar('+'), OperatorFamily, Promotion('+', PlusPlus)),
  Minus(SingleChar('-'), OperatorFamily, Promotion('-', MinusMinus)),
  Multiply(SingleChar('*'), OperatorFamily),
  Divide(SingleChar('/'), OperatorFamily),
  And(ManyChars("and"), OperatorFamily),
  Or(ManyChars("or"), OperatorFamily),
  Tilde(SingleChar('~'), OperatorFamily),
  Ampersand(SingleChar('&'), OperatorFamily),
  Pipe(SingleChar('|'), OperatorFamily),
  Caret(SingleChar('^'), OperatorFamily),
  LesserEqual(OperatorFamily),
  GreaterEqual(OperatorFamily),
  Lesser(SingleChar('<'), OperatorFamily, Promotion('=', LesserEqual)),
  Greater(SingleChar('>'), OperatorFamily, Promotion('=', GreaterEqual)),
  LeftCurly(SingleChar('{')),
  RightCurly(SingleChar('}')),
  LeftBracket(SingleChar('[')),
  RightBracket(SingleChar(']')),
  LeftParentheses(SingleChar('(')),
  RightParentheses(SingleChar(')')),
  Not(ManyChars("not"), OperatorFamily),
  Mod(ManyChars("mod"), OperatorFamily),
  Rem(ManyChars("rem"), OperatorFamily),
  Comma(SingleChar(',')),
  DotDotDot(ManyChars("...")),
  DotDot(ManyChars(".."), Promotion('.', DotDotDot)),
  Dot(SingleChar('.'), Promotion('.', DotDot)),
  False(ManyChars("false"), KeywordFamily),
  True(ManyChars("true"), KeywordFamily),
  If(ManyChars("if"), KeywordFamily),
  Else(ManyChars("else"), KeywordFamily),
  While(ManyChars("while"), KeywordFamily),
  For(ManyChars("for"), KeywordFamily),
  In(ManyChars("in"), KeywordFamily),
  Continue(ManyChars("continue"), KeywordFamily),
  Break(ManyChars("break"), KeywordFamily),
  Import(ManyChars("import"), KeywordFamily),
  Export(ManyChars("export"), KeywordFamily),
  Func(ManyChars("func"), KeywordFamily),
  Native(ManyChars("native"), KeywordFamily),
  Let(ManyChars("let"), KeywordFamily),
  Var(ManyChars("var"), KeywordFamily),
  Whitespace(CharConsumer { it == ' ' || it == '\r' || it == '\n' || it == '\t' }),
  BlockComment(CommentFamily),
  LineComment(CommentFamily),
  EndOfFile;

  val promotions: List<Promotion> =
    properties.filterIsInstance<Promotion>()
  val manyChars: ManyChars? =
    properties.filterIsInstance<ManyChars>().singleOrNull()
  val singleChar: SingleChar? =
    properties.filterIsInstance<SingleChar>().singleOrNull()
  val family: TokenFamily =
    properties.filterIsInstance<TokenFamily>().singleOrNull() ?: OtherFamily
  val charConsumer: CharConsumer? = properties.filterIsInstance<CharConsumer>().singleOrNull()
  val charIndexConsumer: CharIndexConsumer? =
    properties.filterIsInstance<CharIndexConsumer>().singleOrNull()
  val tokenUpgrader: TokenUpgrader? =
    properties.filterIsInstance<TokenUpgrader>().singleOrNull()

  companion object {
    val ManyChars = entries.filter { item -> item.manyChars != null }
    val SingleChars = entries.filter { item -> item.singleChar != null }
    val CharConsumers = entries.filter { item ->
      item.charConsumer != null || item.charIndexConsumer != null }
  }
}
