package gay.pizza.pork.parser

import gay.pizza.pork.ast.gen.*
import gay.pizza.pork.tokenizer.TokenSource
import gay.pizza.pork.tokenizer.TokenType

class Parser(source: TokenSource, attribution: NodeAttribution) :
  ParserBase(source, attribution) {
  override fun parseArgumentSpec(): ArgumentSpec = produce(NodeType.ArgumentSpec) {
    val symbol = parseSymbol()
    ArgumentSpec(symbol, next(TokenType.DotDotDot))
  }

  override fun parseBlock(): Block = expect(NodeType.Block, TokenType.LeftCurly) {
    val items = collect(TokenType.RightCurly) {
      parseExpression()
    }
    expect(TokenType.RightCurly)
    Block(items)
  }

  override fun parseExpression(): Expression {
    val token = peek()
    var expression = when (token.type) {
      TokenType.NumberLiteral -> parseNumberLiteral()
      TokenType.Quote -> parseStringLiteral()
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
      expression = produce(NodeType.SetAssignment) {
        attribution.adopt(expression)
        expect(TokenType.Equals)
        val value = parseExpression()
        SetAssignment(symbolReference.symbol, value)
      }
    }

    if (peek(TokenType.LeftBracket)) {
      expression = produce(NodeType.IndexedBy) {
        attribution.adopt(expression)
        expect(TokenType.LeftBracket)
        val index = parseExpression()
        expect(TokenType.RightBracket)
        IndexedBy(expression, index)
      }
    }

    return if (peek(
        TokenType.Plus, TokenType.Minus, TokenType.Multiply, TokenType.Divide, TokenType.Ampersand,
        TokenType.Pipe, TokenType.Caret, TokenType.Equality, TokenType.Inequality, TokenType.Mod,
        TokenType.Rem, TokenType.Lesser, TokenType.Greater, TokenType.LesserEqual, TokenType.GreaterEqual,
        TokenType.And, TokenType.Or)) {
      produce(NodeType.InfixOperation) {
        val infixToken = next()
        val infixOperator = ParserHelpers.convertInfixOperator(infixToken)
        InfixOperation(expression, infixOperator, parseExpression())
      }
    } else expression
  }

  override fun parseBooleanLiteral(): BooleanLiteral = produce(NodeType.BooleanLiteral) {
    if (next(TokenType.True)) {
      BooleanLiteral(true)
    } else if (next(TokenType.False)) {
      BooleanLiteral(false)
    } else {
      expectedTokenError(source.peek(), TokenType.True, TokenType.False)
    }
  }

  override fun parseBreak(): Break = produce(NodeType.Break) {
    expect(TokenType.Break)
    Break()
  }

  override fun parseCompilationUnit(): CompilationUnit = produce(NodeType.CompilationUnit) {
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

  override fun parseContinue(): Continue = produce(NodeType.Continue) {
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

  override fun parseDoubleLiteral(): DoubleLiteral = expect(NodeType.DoubleLiteral, TokenType.NumberLiteral) {
    DoubleLiteral(it.text.toDouble())
  }

  override fun parseForIn(): ForIn = expect(NodeType.ForIn, TokenType.For) {
    val forInItem = parseForInItem()
    expect(TokenType.In)
    val value = parseExpression()
    val block = parseBlock()
    ForIn(forInItem, value, block)
  }

  override fun parseForInItem(): ForInItem = produce(NodeType.ForInItem) {
    ForInItem(parseSymbol())
  }

  override fun parseFunctionCall(): FunctionCall = produce(NodeType.FunctionCall) {
    val symbol = parseSymbol()
    expect(TokenType.LeftParentheses)
    val arguments = collect(TokenType.RightParentheses, TokenType.Comma) {
      parseExpression()
    }
    expect(TokenType.RightParentheses)
    FunctionCall(symbol, arguments)
  }

  override fun parseFunctionDefinition(): FunctionDefinition = produce(NodeType.FunctionDefinition) {
    val modifiers = parseDefinitionModifiers()
    expect(TokenType.Func)
    val name = parseSymbol()
    expect(TokenType.LeftParentheses)
    val arguments = collect(TokenType.RightParentheses, TokenType.Comma) {
      parseArgumentSpec()
    }
    expect(TokenType.RightParentheses)

    var native: NativeFunctionDescriptor? = null
    var block: Block? = null
    if (peek(TokenType.Native)) {
      native = parseNativeFunctionDescriptor()
    } else {
      block = parseBlock()
    }
    FunctionDefinition(modifiers, name, arguments, block, native)
  }

  override fun parseIf(): If = expect(NodeType.If, TokenType.If) {
    val condition = parseExpression()
    val thenBlock = parseBlock()
    var elseBlock: Block? = null
    if (next(TokenType.Else)) {
      elseBlock = parseBlock()
    }
    If(condition, thenBlock, elseBlock)
  }

  override fun parseImportDeclaration(): ImportDeclaration = expect(NodeType.ImportDeclaration, TokenType.Import) {
    val form = parseSymbol()
    ImportDeclaration(form, parseImportPath())
  }

  override fun parseImportPath(): ImportPath = produce(NodeType.ImportPath) {
    val components = oneAndContinuedBy(TokenType.Dot) {
      parseSymbol()
    }
    ImportPath(components)
  }

  override fun parseIndexedBy(): IndexedBy = produce(NodeType.IndexedBy) {
    val expression = parseExpression()
    expect(TokenType.LeftBracket)
    val index = parseExpression()
    expect(TokenType.RightBracket)
    IndexedBy(expression, index)
  }

  override fun parseInfixOperation(): InfixOperation = produce(NodeType.InfixOperation) {
    val infixToken = next()
    val infixOperator = ParserHelpers.convertInfixOperator(infixToken)
    InfixOperation(parseExpression(), infixOperator, parseExpression())
  }

  private fun parseNumberLiteral(): Expression {
    val token = peek()
    if (token.type != TokenType.NumberLiteral) {
      expect(TokenType.NumberLiteral)
    }

    return when {
      token.text.contains(".") -> parseDoubleLiteral()
      token.text.toIntOrNull() != null -> parseIntegerLiteral()
      token.text.toLongOrNull() != null -> parseLongLiteral()
      else -> throw ParseError("Invalid numeric literal")
    }
  }

  override fun parseIntegerLiteral(): IntegerLiteral = expect(NodeType.IntegerLiteral, TokenType.NumberLiteral) {
    IntegerLiteral(it.text.toInt())
  }

  override fun parseLetAssignment(): LetAssignment = expect(NodeType.LetAssignment, TokenType.Let) {
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    LetAssignment(symbol, value)
  }

  override fun parseLetDefinition(): LetDefinition = produce(NodeType.LetDefinition) {
    val definitionModifiers = parseDefinitionModifiers()
    expect(TokenType.Let)
    val name = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    LetDefinition(definitionModifiers, name, value)
  }

  override fun parseListLiteral(): ListLiteral = expect(NodeType.ListLiteral, TokenType.LeftBracket) {
    val items = collect(TokenType.RightBracket, TokenType.Comma) {
      parseExpression()
    }
    expect(TokenType.RightBracket)
    ListLiteral(items)
  }

  override fun parseLongLiteral(): LongLiteral = expect(NodeType.LongLiteral, TokenType.NumberLiteral) {
    LongLiteral(it.text.toLong())
  }

  override fun parseNativeFunctionDescriptor(): NativeFunctionDescriptor = expect(NodeType.NativeFunctionDescriptor, TokenType.Native) {
    val form = parseSymbol()
    val definitions = mutableListOf<StringLiteral>()
    while (peek(TokenType.Quote)) {
      definitions.add(parseStringLiteral())
    }
    NativeFunctionDescriptor(form, definitions)
  }

  override fun parseNoneLiteral(): NoneLiteral = expect(NodeType.NoneLiteral, TokenType.None) {
    NoneLiteral()
  }

  override fun parseParentheses(): Parentheses = expect(NodeType.Parentheses, TokenType.LeftParentheses) {
    val expression = parseExpression()
    expect(TokenType.RightParentheses)
    Parentheses(expression)
  }

  override fun parsePrefixOperation(): PrefixOperation = expect(
    NodeType.PrefixOperation,
    TokenType.Not, TokenType.Plus,
    TokenType.Minus, TokenType.Tilde
  ) {
    PrefixOperation(ParserHelpers.convertPrefixOperator(it), parseExpression())
  }

  override fun parseSetAssignment(): SetAssignment = produce(NodeType.SetAssignment) {
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    SetAssignment(symbol, value)
  }

  override fun parseStringLiteral(): StringLiteral = produce(NodeType.StringLiteral) {
    expect(TokenType.Quote)
    val stringLiteralToken = expect(TokenType.StringLiteral)
    expect(TokenType.Quote)
    val content = StringEscape.unescape(stringLiteralToken.text)
    StringLiteral(content)
  }

  override fun parseSuffixOperation(): SuffixOperation = produce(NodeType.SuffixOperation) {
    val reference = parseSymbolReference()
    expect(TokenType.PlusPlus, TokenType.MinusMinus) {
      SuffixOperation(ParserHelpers.convertSuffixOperator(it), reference)
    }
  }

  private fun parseSymbolCases(): Expression {
    return if (peek(1, TokenType.LeftParentheses)) {
      parseFunctionCall()
    } else if (peek(1, TokenType.PlusPlus, TokenType.MinusMinus)) {
      parseSuffixOperation()
    } else parseSymbolReference()
  }

  override fun parseSymbol(): Symbol = expect(NodeType.Symbol, TokenType.Symbol) {
    Symbol(it.text)
  }

  override fun parseSymbolReference(): SymbolReference = produce(NodeType.SymbolReference) {
    SymbolReference(parseSymbol())
  }

  override fun parseVarAssignment(): VarAssignment = expect(NodeType.VarAssignment, TokenType.Var) {
    val symbol = parseSymbol()
    expect(TokenType.Equals)
    val value = parseExpression()
    VarAssignment(symbol, value)
  }

  override fun parseWhile(): While = expect(NodeType.While, TokenType.While) {
    val condition = parseExpression()
    val block = parseBlock()
    While(condition, block)
  }
}
