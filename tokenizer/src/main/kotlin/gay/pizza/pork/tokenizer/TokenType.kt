package gay.pizza.pork.tokenizer

import gay.pizza.pork.tokenizer.CharMatcher.*
import gay.pizza.pork.tokenizer.MatchedCharConsumer.Options.AllowEofTermination
import gay.pizza.pork.tokenizer.TokenTypeProperty.*
import gay.pizza.pork.tokenizer.TokenFamily.*
import gay.pizza.pork.tokenizer.TokenTypeProperty.AnyOf

enum class TokenType(vararg val properties: TokenTypeProperty) {
  NumberLiteral(NumericLiteralFamily, CharMatch(CharMatcher.AnyOf(
    MatchRange('0'..'9'),
    NotAtIndex(0, MatchSingle('.'))
  ))),
  Symbol(SymbolFamily, CharMatch(CharMatcher.AnyOf(
    MatchRange('a'..'z'),
    MatchRange('A'..'Z'),
    MatchRange('0' .. '9'),
    MatchSingle('_')
  )), KeywordUpgrader),
  Quote(StringLiteralFamily, SingleChar('"'), InsideStates(TokenizerState.Normal, TokenizerState.StringLiteralEnd)),
  StringLiteral(StringLiteralFamily, CharConsume(StringCharConsumer), InsideStates(TokenizerState.StringLiteralStart)),
  Equality(OperatorFamily),
  Inequality(ManyChars("!="), OperatorFamily),
  ExclamationPoint(SingleChar('!'), Promotion('=', Inequality)),
  None(ManyChars("None"), KeywordFamily),
  Equals(SingleChar('='), Promotion('=', Equality)),
  PlusPlus(ManyChars("++"), OperatorFamily),
  MinusMinus(ManyChars("--"), OperatorFamily),
  Plus(SingleChar('+'), OperatorFamily, Promotion('+', PlusPlus)),
  Minus(SingleChar('-'), OperatorFamily, Promotion('-', MinusMinus)),
  Multiply(SingleChar('*'), OperatorFamily),
  Divide(SingleChar('/'), OperatorFamily),
  And(ManyChars("and"), KeywordFamily),
  Or(ManyChars("or"), KeywordFamily),
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
  Not(ManyChars("not"), KeywordFamily),
  Mod(ManyChars("mod"), KeywordFamily),
  Rem(ManyChars("rem"), KeywordFamily),
  Comma(SingleChar(',')),
  Colon(SingleChar(':')),
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
  Return(ManyChars("return"), KeywordFamily),
  Import(AnyOf("import", "impork", "porkload"), KeywordFamily),
  Export(ManyChars("export"), KeywordFamily),
  Func(ManyChars("func"), KeywordFamily),
  Type(ManyChars("type"), KeywordFamily),
  Native(ManyChars("native"), KeywordFamily),
  Let(ManyChars("let"), KeywordFamily),
  Var(ManyChars("var"), KeywordFamily),
  Whitespace(CharMatch(CharMatcher.AnyOf(
    MatchSingle(' '),
    MatchSingle('\r'),
    MatchSingle('\n'),
    MatchSingle('\t')
  ))),
  BlockComment(CharConsume(MatchedCharConsumer("/*", "*/")), CommentFamily),
  LineComment(CharConsume(MatchedCharConsumer("//", "\n", AllowEofTermination)), CommentFamily),
  EndOfFile;

  val promotions: List<Promotion> =
    properties.filterIsInstance<Promotion>()
  val manyChars: ManyChars? =
    properties.filterIsInstance<ManyChars>().singleOrNull()
  val anyOf: AnyOf? =
    properties.filterIsInstance<AnyOf>().singleOrNull()
  val singleChar: SingleChar? =
    properties.filterIsInstance<SingleChar>().singleOrNull()
  val family: TokenFamily =
    properties.filterIsInstance<TokenFamily>().singleOrNull() ?: OtherFamily
  val charMatch: CharMatch? = properties.filterIsInstance<CharMatch>().singleOrNull()
  val charConsume: CharConsume? = properties.filterIsInstance<CharConsume>().singleOrNull()
  val tokenUpgrader: TokenUpgrader? =
    properties.filterIsInstance<TokenUpgrader>().singleOrNull()
  val validStates: List<TokenizerState> by lazy {
    properties
      .filterIsInstance<InsideStates>()
      .singleOrNull()?.states?.toList() ?: listOf(TokenizerState.Normal)
  }

  val simpleWantString: String? = manyChars?.text ?: singleChar?.char?.toString()

  companion object {
    val AnyOf = entries.filter { item -> item.anyOf != null }
    val ManyChars = entries.filter { item -> item.manyChars != null }
    val SingleChars = entries.filter { item -> item.singleChar != null }
    val CharMatches = entries.filter { item -> item.charMatch != null }
    val CharConsumes = entries.filter { item -> item.charConsume != null }

    val ParserIgnoredTypes: Set<TokenType> = setOf(
      Whitespace,
      BlockComment,
      LineComment
    )

    val DeclarationModifiers: Array<TokenType> = arrayOf(
      Export
    )
  }
}
