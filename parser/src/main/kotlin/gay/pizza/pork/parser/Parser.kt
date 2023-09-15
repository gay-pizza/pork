package gay.pizza.pork.parser

import gay.pizza.pork.ast.*

class Parser(source: TokenSource, attribution: NodeAttribution) :
  ParserBase(source, attribution) {
  private var storedSymbol: Symbol? = null
  private var storedDefinitionModifiers: DefinitionModifiers? = null

  override fun parseBlock(): Block = guarded {
    expect(TokenType.LeftCurly)
    val items = collect(TokenType.RightCurly) {
      parseExpression()
    }
    expect(TokenType.RightCurly)
    Block(items)
  }

  override fun parseExpression(): Expression = guarded {
    val token = peek()
    val expression = when (token.type) {
      TokenType.NumberLiteral -> parseNumberLiteral()
      TokenType.StringLiteral -> parseStringLiteral()
      TokenType.True, TokenType.False -> parseBooleanLiteral()
      TokenType.LeftBracket -> parseListLiteral()
      TokenType.Let -> parseLetAssignment()
      TokenType.Var -> parseVarAssignment()
      TokenType.Symbol -> parseSymbolCases()
      TokenType.LeftParentheses -> parseParentheses()
      TokenType.Not, TokenType.Plus, TokenType.Minus, TokenType.Tilde ->
        parsePrefixOperation()
      TokenType.If -> parseIf()
      TokenType.While -> parseWhile()
      TokenType.For -> parseForIn()
      TokenType.Break -> parseBreak()
      TokenType.Continue -> parseContinue()
      TokenType.None -> parseNoneLiteral()

      else -> {
        throw ParseError(
          "Failed to parse token: ${token.type} '${token.text}' as" +
            " expression (index ${source.currentIndex})"
        )
      }
    }

    if (expression is SymbolReference && peek(TokenType.Equals)) {
      return@guarded guarded {
        attribution.adopt(expression)
        expect(TokenType.Equals)
        val value = parseExpression()
        SetAssignment(expression.symbol, value)
      }
    }

    return@guarded if (peek(
        TokenType.Plus,
        TokenType.Minus,
        TokenType.Multiply,
        TokenType.Divide,
        TokenType.Ampersand,
        TokenType.Pipe,
        TokenType.Caret,
        TokenType.Equality,
        TokenType.Inequality,
        TokenType.Mod,
        TokenType.Rem,
        TokenType.Lesser,
        TokenType.Greater,
        TokenType.LesserEqual,
        TokenType.GreaterEqual,
        TokenType.And,
        TokenType.Or
      )
    ) {
      guarded {
        val infixToken = next()
        val infixOperator = ParserHelpers.convertInfixOperator(infixToken)
        InfixOperation(expression, infixOperator, parseExpression())
      }
    } else expression
  }

  override fun parseBooleanLiteral(): BooleanLiteral = guarded {
    if (next(TokenType.True)) {
      BooleanLiteral(true)
    } else if (next(TokenType.False)) {
      BooleanLiteral(false)
    } else {
      throw ParseError("Expected ")
    }
  }

  override fun parseBreak(): Break = guarded {
    expect(TokenType.Break)
    Break()
  }

  override fun parseCompilationUnit(): CompilationUnit = guarded {
    val declarations = mutableListOf<Declaration>()
    val definitions = mutableListOf<Definition>()
    var declarationAccepted = true

    while (!peek(TokenType.EndOfFile)) {
      if (declarationAccepted) {
        val definition = maybeParseDefinition()
        if (definition != null) {
          declarationAccepted = false
          definitions.add(definition)
          continue
        }
        declarations.add(parseDeclaration())
      } else {
        definitions.add(parseDefinition())
      }
    }

    CompilationUnit(declarations, definitions)
  }

  override fun parseContinue(): Continue = guarded {
    expect(TokenType.Continue)
    Continue()
  }

  private fun parseDefinitionModifiers(): DefinitionModifiers {
    val modifiers = DefinitionModifiers(export = false)
    while (true) {
      val token = peek()
      when (token.type) {
        TokenType.Export -> {
          expect(TokenType.Export)
          modifiers.export = true
        }
        else -> break
      }
    }
    return modifiers
  }

  private fun maybeParseDefinition(): Definition? {
    try {
      storedDefinitionModifiers = parseDefinitionModifiers()
      val token = peek()
      return when (token.type) {
        TokenType.Func -> parseFunctionDefinition()
        TokenType.Let -> parseLetDefinition()
        else -> null
      }
    } finally {
      storedDefinitionModifiers = null
    }
  }

  override fun parseDeclaration(): Declaration = guarded {
    val token = peek()
    return@guarded when (token.type) {
      TokenType.Import -> parseImportDeclaration()
      else -> throw ParseError(
        "Failed to parse token: ${token.type} '${token.text}' as" +
          " declaration (index ${source.currentIndex})"
      )
    }
  }

  override fun parseDefinition(): Definition = guarded {
    maybeParseDefinition() ?: throw ParseError("Unable to parse definition")
  }

  override fun parseDoubleLiteral(): DoubleLiteral = guarded {
    DoubleLiteral(expect(TokenType.NumberLiteral).text.toDouble())
  }

  override fun parseForIn(): ForIn = guarded {
    expect(TokenType.For)
    val symbol = parseSymbol()
    expect(TokenType.In)
    val value = parseExpression()
    val block = parseBlock()
    ForIn(symbol, value, block)
  }

  override fun parseFunctionCall(): FunctionCall = guarded {
    parseFunctionCall(null)
  }

  fun parseFunctionCall(target: Symbol?): FunctionCall = guarded {
    val symbol = target ?: parseSymbol()
    val arguments = collect(TokenType.RightParentheses, TokenType.Comma) {
      parseExpression()
    }
    expect(TokenType.RightParentheses)
    FunctionCall(symbol, arguments)
  }

  override fun parseFunctionDefinition(): FunctionDefinition = guarded {
    val modifiers = storedDefinitionModifiers ?: parseDefinitionModifiers()
    expect(TokenType.Func)
    val name = parseSymbol()
    expect(TokenType.LeftParentheses)
    val arguments = collect(TokenType.RightParentheses, TokenType.Comma) {
      val symbol = parseSymbol()
      var multiple = false
      if (next(TokenType.DotDotDot)) {
        multiple = true
      }
      ArgumentSpec(symbol, multiple)
    }
    expect(TokenType.RightParentheses)

    var native: Native? = null
    var block: Block? = null
    if (peek(TokenType.Native)) {
      native = parseNative()
    } else {
      block = parseBlock()
    }
    FunctionDefinition(modifiers, name, arguments, block, native)
  }

  override fun parseIf(): If = guarded {
    expect(TokenType.If)
    val condition = parseExpression()
    val thenBlock = parseBlock()
    var elseBlock: Block? = null
    if (next(TokenType.Else)) {
      elseBlock = parseBlock()
    }
    If(condition, thenBlock, elseBlock)
  }

  override fun parseImportDeclaration(): ImportDeclaration = guarded {
    expect(TokenType.Import, TokenType.Impork, TokenType.PorkLoad)
    val form = parseSymbol()
    val components = oneAndContinuedBy(TokenType.Dot) {
      parseSymbol()
    }
    ImportDeclaration(form, components)
  }

  override fun parseInfixOperation(): InfixOperation = guarded {
    val infixToken = next()
    val infixOperator = ParserHelpers.convertInfixOperator(infixToken)
    InfixOperation(parseExpression(), infixOperator, parseExpression())
  }

  private fun parseNumberLiteral(): Expression = guarded {
    val token = peek()
    if (token.type != TokenType.NumberLiteral) {
      expect(TokenType.NumberLiteral)
    }

    when {
      token.text.contains(".") -> parseDoubleLiteral()
      token.text.toIntOrNull() != null -> parseIntegerLiteral()
      token.text.toLongOrNull() != null -> parseLongLiteral()
      else -> throw ParseError("Invalid numeric literal")
    }
  }

  override fun parseIntegerLiteral(): IntegerLiteral = guarded {
    IntegerLiteral(expect(TokenType.NumberLiteral).text.toInt())
  }

  override fun parseLetAssignment(): LetAssignment = guarded {
    expect(TokenType.Let)
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    LetAssignment(symbol, value)
  }

  override fun parseLetDefinition(): LetDefinition = guarded {
    val definitionModifiers = storedDefinitionModifiers ?: parseDefinitionModifiers()
    expect(TokenType.Let)
    val name = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    LetDefinition(definitionModifiers, name, value)
  }

  override fun parseListLiteral(): ListLiteral = guarded {
    expect(TokenType.LeftBracket)
    val items = collect(TokenType.RightBracket, TokenType.Comma) {
      parseExpression()
    }
    expect(TokenType.RightBracket)
    ListLiteral(items)
  }

  override fun parseLongLiteral(): LongLiteral = guarded {
    LongLiteral(expect(TokenType.NumberLiteral).text.toLong())
  }

  override fun parseNative(): Native = guarded {
    expect(TokenType.Native)
    val form = parseSymbol()
    val definition = parseStringLiteral()
    Native(form, definition)
  }

  override fun parseNoneLiteral(): NoneLiteral = guarded {
    expect(TokenType.None)
    NoneLiteral()
  }

  override fun parseParentheses(): Parentheses = guarded {
    expect(TokenType.LeftParentheses)
    val expression = parseExpression()
    expect(TokenType.RightParentheses)
    Parentheses(expression)
  }

  override fun parsePrefixOperation(): PrefixOperation = guarded {
    expect(TokenType.Not, TokenType.Plus, TokenType.Minus, TokenType.Tilde) {
      PrefixOperation(ParserHelpers.convertPrefixOperator(it), parseExpression())
    }
  }

  override fun parseSetAssignment(): SetAssignment = guarded {
    val symbol = storedSymbol ?: parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    SetAssignment(symbol, value)
  }

  override fun parseStringLiteral(): StringLiteral = guarded {
    expect(TokenType.StringLiteral) {
      val content = StringEscape.unescape(StringEscape.unquote(it.text))
      StringLiteral(content)
    }
  }

  override fun parseSuffixOperation(): SuffixOperation = guarded {
    val reference = parseSymbolReference()
    expect(TokenType.PlusPlus, TokenType.MinusMinus) {
      SuffixOperation(ParserHelpers.convertSuffixOperator(it), reference)
    }
  }

  private fun parseSymbolCases(): Expression = guarded {
    val symbol = parseSymbol()
    if (next(TokenType.LeftParentheses)) {
      parseFunctionCall(symbol)
    } else {
      val reference = SymbolReference(symbol)
      if (peek(TokenType.PlusPlus, TokenType.MinusMinus)) {
        expect(TokenType.PlusPlus, TokenType.MinusMinus) {
          SuffixOperation(ParserHelpers.convertSuffixOperator(it), reference)
        }
      } else reference
    }
  }

  override fun parseSymbol(): Symbol = guarded {
    expect(TokenType.Symbol) { Symbol(it.text) }
  }

  override fun parseSymbolReference(): SymbolReference = guarded {
    SymbolReference(parseSymbol())
  }

  override fun parseVarAssignment(): VarAssignment = guarded {
    expect(TokenType.Var)
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    VarAssignment(symbol, value)
  }

  override fun parseWhile(): While = guarded {
    expect(TokenType.While)
    val condition = parseExpression()
    val block = parseBlock()
    While(condition, block)
  }
}
