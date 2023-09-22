package gay.pizza.pork.parser

import gay.pizza.pork.ast.*

class Parser(source: TokenSource, attribution: NodeAttribution) :
  ParserBase(source, attribution) {
  override fun parseBlock(): Block = guarded(NodeType.Block) {
    expect(TokenType.LeftCurly)
    val items = collect(TokenType.RightCurly) {
      parseExpression()
    }
    expect(TokenType.RightCurly)
    Block(items)
  }

  override fun parseExpression(): Expression = guarded {
    val token = peek()
    var expression = when (token.type) {
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
      val symbolReference = expression
      expression = guarded(NodeType.SetAssignment) {
        attribution.adopt(expression)
        expect(TokenType.Equals)
        val value = parseExpression()
        SetAssignment(symbolReference.symbol, value)
      }
    }

    if (peek(TokenType.LeftBracket)) {
      expression = guarded(NodeType.IndexedBy) {
        attribution.adopt(expression)
        expect(TokenType.LeftBracket)
        val index = parseExpression()
        expect(TokenType.RightBracket)
        IndexedBy(expression, index)
      }
    }

    if (peek(
        TokenType.Plus, TokenType.Minus, TokenType.Multiply, TokenType.Divide, TokenType.Ampersand,
        TokenType.Pipe, TokenType.Caret, TokenType.Equality, TokenType.Inequality, TokenType.Mod,
        TokenType.Rem, TokenType.Lesser, TokenType.Greater, TokenType.LesserEqual, TokenType.GreaterEqual,
        TokenType.And, TokenType.Or)) {
      guarded(NodeType.InfixOperation) {
        val infixToken = next()
        val infixOperator = ParserHelpers.convertInfixOperator(infixToken)
        InfixOperation(expression, infixOperator, parseExpression())
      }
    } else expression
  }

  override fun parseBooleanLiteral(): BooleanLiteral = guarded(NodeType.BooleanLiteral) {
    if (next(TokenType.True)) {
      BooleanLiteral(true)
    } else if (next(TokenType.False)) {
      BooleanLiteral(false)
    } else {
      expectedTokenError(source.peek(), TokenType.True, TokenType.False)
    }
  }

  override fun parseBreak(): Break = guarded(NodeType.Break) {
    expect(TokenType.Break)
    Break()
  }

  override fun parseCompilationUnit(): CompilationUnit = guarded(NodeType.CompilationUnit) {
    val declarations = mutableListOf<Declaration>()
    val definitions = mutableListOf<Definition>()
    var declarationAccepted = true

    while (!peek(TokenType.EndOfFile)) {
      if (declarationAccepted) {
        val declaration = parseDeclarationMaybe()
        if (declaration != null) {
          declarations.add(declaration)
          continue
        } else {
          declarationAccepted = false
        }
      }
      definitions.add(parseDefinition())
    }

    CompilationUnit(declarations, definitions)
  }

  override fun parseContinue(): Continue = guarded(NodeType.Continue) {
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

  fun peekAheadUntilNotIn(vararg types: TokenType): TokenType {
    var i = 0
    while (true) {
      val token = peek(i)
      if (!types.contains(token)) {
        return token
      }
      i++
    }
  }

  private fun parseDeclarationMaybe(): Declaration? {
    val token = peek()
    return when (token.type) {
      TokenType.Import -> parseImportDeclaration()
      else -> null
    }
  }

  override fun parseDeclaration(): Declaration {
    val declaration = parseDeclarationMaybe()
    if (declaration == null) {
      val token = peek()
      throw ParseError(
        "Failed to parse token: ${token.type} '${token.text}' as" +
          " declaration (index ${source.currentIndex})"
      )
    }
    return declaration
  }

  override fun parseDefinition(): Definition =
    when (val type = peekAheadUntilNotIn(*TokenType.DeclarationModifiers)) {
      TokenType.Func -> parseFunctionDefinition()
      TokenType.Let -> parseLetDefinition()
      else -> throw ParseError(
        "Failed to parse token: ${type.name} as" +
          " declaration (index ${source.currentIndex})"
      )
    }

  override fun parseDoubleLiteral(): DoubleLiteral = guarded(NodeType.DoubleLiteral) {
    DoubleLiteral(expect(TokenType.NumberLiteral).text.toDouble())
  }

  override fun parseForIn(): ForIn = guarded(NodeType.ForIn) {
    expect(TokenType.For)
    val symbol = parseSymbol()
    expect(TokenType.In)
    val value = parseExpression()
    val block = parseBlock()
    ForIn(symbol, value, block)
  }

  override fun parseFunctionCall(): FunctionCall = guarded(NodeType.FunctionCall) {
    val symbol = parseSymbol()
    expect(TokenType.LeftParentheses)
    val arguments = collect(TokenType.RightParentheses, TokenType.Comma) {
      parseExpression()
    }
    expect(TokenType.RightParentheses)
    FunctionCall(symbol, arguments)
  }

  override fun parseFunctionDefinition(): FunctionDefinition = guarded(NodeType.FunctionDefinition) {
    val modifiers = parseDefinitionModifiers()
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

  override fun parseIf(): If = guarded(NodeType.If) {
    expect(TokenType.If)
    val condition = parseExpression()
    val thenBlock = parseBlock()
    var elseBlock: Block? = null
    if (next(TokenType.Else)) {
      elseBlock = parseBlock()
    }
    If(condition, thenBlock, elseBlock)
  }

  override fun parseImportDeclaration(): ImportDeclaration = guarded(NodeType.ImportDeclaration) {
    expect(TokenType.Import)
    val form = parseSymbol()
    val components = oneAndContinuedBy(TokenType.Dot) {
      parseSymbol()
    }
    ImportDeclaration(form, components)
  }

  override fun parseIndexedBy(): IndexedBy = guarded(NodeType.IndexedBy) {
    val expression = parseExpression()
    expect(TokenType.LeftBracket)
    val index = parseExpression()
    expect(TokenType.RightBracket)
    IndexedBy(expression, index)
  }

  override fun parseInfixOperation(): InfixOperation = guarded(NodeType.InfixOperation) {
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

  override fun parseIntegerLiteral(): IntegerLiteral = guarded(NodeType.IntegerLiteral) {
    IntegerLiteral(expect(TokenType.NumberLiteral).text.toInt())
  }

  override fun parseLetAssignment(): LetAssignment = guarded(NodeType.LetAssignment) {
    expect(TokenType.Let)
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    LetAssignment(symbol, value)
  }

  override fun parseLetDefinition(): LetDefinition = guarded(NodeType.LetDefinition) {
    val definitionModifiers = parseDefinitionModifiers()
    expect(TokenType.Let)
    val name = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    LetDefinition(definitionModifiers, name, value)
  }

  override fun parseListLiteral(): ListLiteral = guarded(NodeType.ListLiteral) {
    expect(TokenType.LeftBracket)
    val items = collect(TokenType.RightBracket, TokenType.Comma) {
      parseExpression()
    }
    expect(TokenType.RightBracket)
    ListLiteral(items)
  }

  override fun parseLongLiteral(): LongLiteral = guarded(NodeType.LongLiteral) {
    LongLiteral(expect(TokenType.NumberLiteral).text.toLong())
  }

  override fun parseNative(): Native = guarded(NodeType.Native) {
    expect(TokenType.Native)
    val form = parseSymbol()
    val definition = parseStringLiteral()
    Native(form, definition)
  }

  override fun parseNoneLiteral(): NoneLiteral = guarded(NodeType.NoneLiteral) {
    expect(TokenType.None)
    NoneLiteral()
  }

  override fun parseParentheses(): Parentheses = guarded(NodeType.Parentheses) {
    expect(TokenType.LeftParentheses)
    val expression = parseExpression()
    expect(TokenType.RightParentheses)
    Parentheses(expression)
  }

  override fun parsePrefixOperation(): PrefixOperation = guarded(NodeType.PrefixOperation) {
    expect(TokenType.Not, TokenType.Plus, TokenType.Minus, TokenType.Tilde) {
      PrefixOperation(ParserHelpers.convertPrefixOperator(it), parseExpression())
    }
  }

  override fun parseSetAssignment(): SetAssignment = guarded(NodeType.SetAssignment) {
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    SetAssignment(symbol, value)
  }

  override fun parseStringLiteral(): StringLiteral = guarded(NodeType.StringLiteral) {
    expect(TokenType.StringLiteral) {
      val content = StringEscape.unescape(StringEscape.unquote(it.text))
      StringLiteral(content)
    }
  }

  override fun parseSuffixOperation(): SuffixOperation = guarded(NodeType.SuffixOperation) {
    val reference = parseSymbolReference()
    expect(TokenType.PlusPlus, TokenType.MinusMinus) {
      SuffixOperation(ParserHelpers.convertSuffixOperator(it), reference)
    }
  }

  private fun parseSymbolCases(): Expression = guarded {
    if (peek(1, TokenType.LeftParentheses)) {
      parseFunctionCall()
    } else if (peek(1, TokenType.PlusPlus, TokenType.MinusMinus)) {
      parseSuffixOperation()
    } else parseSymbolReference()
  }

  override fun parseSymbol(): Symbol = guarded(NodeType.Symbol) {
    expect(TokenType.Symbol) { Symbol(it.text) }
  }

  override fun parseSymbolReference(): SymbolReference = guarded(NodeType.SymbolReference) {
    SymbolReference(parseSymbol())
  }

  override fun parseVarAssignment(): VarAssignment = guarded(NodeType.VarAssignment) {
    expect(TokenType.Var)
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    VarAssignment(symbol, value)
  }

  override fun parseWhile(): While = guarded(NodeType.While) {
    expect(TokenType.While)
    val condition = parseExpression()
    val block = parseBlock()
    While(condition, block)
  }
}
