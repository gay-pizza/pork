package gay.pizza.pork.parser

import gay.pizza.pork.ast.*

class Parser(source: PeekableSource<Token>, val attribution: NodeAttribution) {
  private val unsanitizedSource = source

  private fun readNumberLiteral(): Expression = within {
    expect(TokenType.NumberLiteral) {
      if (it.text.contains(".")) {
        DoubleLiteral(it.text.toDouble())
      } else {
        IntegerLiteral(it.text.toInt())
      }
    }
  }

  private fun readStringLiteral(): StringLiteral = within {
    expect(TokenType.StringLiteral) {
      val content = StringEscape.unescape(StringEscape.unquote(it.text))
      StringLiteral(content)
    }
  }

  private fun readBooleanLiteral(): BooleanLiteral = within {
    expect(TokenType.True, TokenType.False) {
      BooleanLiteral(it.type == TokenType.True)
    }
  }

  private fun readListLiteral(): ListLiteral = within {
    expect(TokenType.LeftBracket)
    val items = collect(TokenType.RightBracket, TokenType.Comma) {
      readExpression()
    }
    expect(TokenType.RightBracket)
    ListLiteral(items)
  }

  private fun readLetAssignment(): LetAssignment = within {
    expect(TokenType.Let)
    val symbol = readSymbolRaw()
    expect(TokenType.Equals)
    val value = readExpression()
    LetAssignment(symbol, value)
  }

  private fun readVarAssignment(): VarAssignment = within {
    expect(TokenType.Var)
    val symbol = readSymbolRaw()
    expect(TokenType.Equals)
    val value = readExpression()
    VarAssignment(symbol, value)
  }

  private fun readSymbolRaw(): Symbol = within {
    expect(TokenType.Symbol) { Symbol(it.text) }
  }

  private fun readSymbolCases(): Expression = within {
    val symbol = readSymbolRaw()
    if (next(TokenType.LeftParentheses)) {
      val arguments = collect(TokenType.RightParentheses, TokenType.Comma) {
        readExpression()
      }
      expect(TokenType.RightParentheses)
      FunctionCall(symbol, arguments)
    } else {
      val reference = SymbolReference(symbol)
      if (peek(TokenType.PlusPlus, TokenType.MinusMinus)) {
        expect(TokenType.PlusPlus, TokenType.MinusMinus) {
          SuffixOperation(convertSuffixOperator(it), reference)
        }
      } else reference
    }
  }

  private fun readParentheses(): Parentheses = within {
    expect(TokenType.LeftParentheses)
    val expression = readExpression()
    expect(TokenType.RightParentheses)
    Parentheses(expression)
  }

  private fun readPrefixOperation(): PrefixOperation = within {
    expect(TokenType.Negation, TokenType.Plus, TokenType.Minus) {
      PrefixOperation(convertPrefixOperator(it), readExpression())
    }
  }

  private fun readIf(): If = within {
    expect(TokenType.If)
    val condition = readExpression()
    val thenBlock = readBlock()
    var elseBlock: Block? = null
    if (next(TokenType.Else)) {
      elseBlock = readBlock()
    }
    If(condition, thenBlock, elseBlock)
  }

  private fun readWhile(): While = within {
    expect(TokenType.While)
    val condition = readExpression()
    val block = readBlock()
    While(condition, block)
  }

  private fun readNative(): Native = within {
    expect(TokenType.Native)
    val form = readSymbolRaw()
    val definition = readStringLiteral()
    Native(form, definition)
  }

  fun readExpression(): Expression {
    val token = peek()
    val expression = when (token.type) {
      TokenType.NumberLiteral -> {
        readNumberLiteral()
      }

      TokenType.StringLiteral -> {
        readStringLiteral()
      }

      TokenType.True, TokenType.False -> {
        readBooleanLiteral()
      }

      TokenType.LeftBracket -> {
        readListLiteral()
      }

      TokenType.Let -> {
        readLetAssignment()
      }

      TokenType.Var -> {
        readVarAssignment()
      }

      TokenType.Symbol -> {
        readSymbolCases()
      }

      TokenType.LeftParentheses -> {
        readParentheses()
      }

      TokenType.Negation, TokenType.Plus, TokenType.Minus -> {
        readPrefixOperation()
      }

      TokenType.If -> {
        readIf()
      }

      TokenType.While -> {
        readWhile()
      }

      TokenType.Break -> {
        expect(TokenType.Break)
        Break()
      }

      TokenType.Continue -> {
        expect(TokenType.Continue)
        Continue()
      }

      else -> {
        throw RuntimeException(
          "Failed to parse token: ${token.type} '${token.text}' as" +
            " expression (index ${unsanitizedSource.currentIndex})"
        )
      }
    }

    if (expression is SymbolReference && peek(TokenType.Equals)) {
      return within {
        attribution.adopt(expression)
        expect(TokenType.Equals)
        val value = readExpression()
        SetAssignment(expression.symbol, value)
      }
    }

    return if (peek(
        TokenType.Plus,
        TokenType.Minus,
        TokenType.Multiply,
        TokenType.Divide,
        TokenType.Equality,
        TokenType.Inequality,
        TokenType.Mod,
        TokenType.Rem,
        TokenType.Lesser,
        TokenType.Greater,
        TokenType.LesserEqual,
        TokenType.GreaterEqual
      )
    ) {
      within {
        val infixToken = next()
        val infixOperator = convertInfixOperator(infixToken)
        InfixOperation(expression, infixOperator, readExpression())
      }
    } else expression
  }

  private fun readBlock(): Block = within {
    expect(TokenType.LeftCurly)
    val items = collect(TokenType.RightCurly) {
      readExpression()
    }
    expect(TokenType.RightCurly)
    Block(items)
  }

  private fun readImportDeclaration(): ImportDeclaration = within {
    expect(TokenType.Import)
    val form = readSymbolRaw()
    val components = oneAndContinuedBy(TokenType.Dot) { readSymbolRaw() }
    ImportDeclaration(form, components)
  }

  private fun readFunctionDeclaration(): FunctionDefinition = within {
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
    expect(TokenType.Func)
    val name = readSymbolRaw()
    expect(TokenType.LeftParentheses)
    val arguments = collect(TokenType.RightParentheses, TokenType.Comma) {
      val symbol = readSymbolRaw()
      var multiple: Boolean = false
      if (next(TokenType.DotDotDot)) {
        multiple = true
      }
      ArgumentSpec(symbol, multiple)
    }
    expect(TokenType.RightParentheses)

    var native: Native? = null
    var block: Block? = null
    if (peek(TokenType.Native)) {
      native = readNative()
    } else {
      block = readBlock()
    }
    FunctionDefinition(modifiers, name, arguments, block, native)
  }

  private fun maybeReadDefinition(): Definition? {
    val token = peek()
    return when (token.type) {
      TokenType.Export,
      TokenType.Func -> readFunctionDeclaration()
      else -> null
    }
  }

  private fun readDefinition(): Definition {
    val definition = maybeReadDefinition()
    if (definition != null) {
      return definition
    }
    val token = peek()
    throw RuntimeException(
      "Failed to parse token: ${token.type} '${token.text}' as" +
        " definition (index ${unsanitizedSource.currentIndex})"
    )
  }

  fun readDeclaration(): Declaration {
    val token = peek()
    return when (token.type) {
      TokenType.Import -> readImportDeclaration()
      else -> throw RuntimeException(
        "Failed to parse token: ${token.type} '${token.text}' as" +
          " declaration (index ${unsanitizedSource.currentIndex})"
      )
    }
  }

  private fun convertInfixOperator(token: Token): InfixOperator = when (token.type) {
    TokenType.Plus -> InfixOperator.Plus
    TokenType.Minus -> InfixOperator.Minus
    TokenType.Multiply -> InfixOperator.Multiply
    TokenType.Divide -> InfixOperator.Divide
    TokenType.Equality -> InfixOperator.Equals
    TokenType.Inequality -> InfixOperator.NotEquals
    TokenType.Mod -> InfixOperator.EuclideanModulo
    TokenType.Rem -> InfixOperator.Remainder
    TokenType.Lesser -> InfixOperator.Lesser
    TokenType.Greater -> InfixOperator.Greater
    TokenType.LesserEqual -> InfixOperator.LesserEqual
    TokenType.GreaterEqual -> InfixOperator.GreaterEqual
    else -> throw RuntimeException("Unknown Infix Operator")
  }

  private fun convertPrefixOperator(token: Token): PrefixOperator = when (token.type) {
    TokenType.Plus -> PrefixOperator.UnaryPlus
    TokenType.Minus -> PrefixOperator.UnaryMinus
    else -> throw RuntimeException("Unknown Prefix Operator")
  }

  private fun convertSuffixOperator(token: Token): SuffixOperator = when (token.type) {
    TokenType.PlusPlus -> SuffixOperator.Increment
    TokenType.MinusMinus -> SuffixOperator.Decrement
    else -> throw RuntimeException("Unknown Suffix Operator")
  }

  fun readCompilationUnit(): CompilationUnit = within {
    val declarations = mutableListOf<Declaration>()
    val definitions = mutableListOf<Definition>()
    var declarationAccepted = true

    while (!peek(TokenType.EndOfFile)) {
      if (declarationAccepted) {
        val definition = maybeReadDefinition()
        if (definition != null) {
          declarationAccepted = false
          definitions.add(definition)
          continue
        }
        declarations.add(readDeclaration())
      } else {
        definitions.add(readDefinition())
      }
    }

    CompilationUnit(declarations, definitions)
  }

  private fun <T> collect(
    peeking: TokenType,
    consuming: TokenType? = null,
    expecting: Array<TokenType>? = null,
    expectingIgnoreType: IgnoreType = IgnoreType.Default,
    read: () -> T
  ): List<T> {
    val items = mutableListOf<T>()
    while (!peek(peeking)) {
      val item = read()
      if (consuming != null) {
        next(consuming)
      }
      if (expecting != null) {
        expect(expectingIgnoreType, *expecting)
      }
      items.add(item)
    }
    return items
  }

  private fun <T> oneAndContinuedBy(separator: TokenType, read: () -> T): List<T> {
    val items = mutableListOf<T>()
    items.add(read())
    while (peek(separator)) {
      expect(separator)
      items.add(read())
    }
    return items
  }

  private fun peek(vararg types: TokenType): Boolean {
    val token = peek()
    return types.contains(token.type)
  }

  private fun next(type: TokenType): Boolean {
    return if (peek(type)) {
      expect(type)
      true
    } else false
  }

  private fun expect(ignoreType: IgnoreType, vararg types: TokenType): Token {
    val token = next(ignoreType)
    if (!types.contains(token.type)) {
      throw RuntimeException(
        "Expected one of ${types.joinToString(", ")}" +
          " but got type ${token.type} '${token.text}'"
      )
    }
    return token
  }

  private fun expect(vararg types: TokenType): Token = expect(IgnoreType.Default, *types)

  private fun <T: Node> expect(vararg types: TokenType, consume: (Token) -> T): T =
    consume(expect(*types))

  private fun next(ignoreType: IgnoreType = IgnoreType.Default): Token {
    while (true) {
      val token = unsanitizedSource.next()
      attribution.push(token)
      if (ignoredByParser(ignoreType, token.type)) {
        continue
      }
      return token
    }
  }

  private fun peek(ignoreType: IgnoreType = IgnoreType.Default): Token {
    while (true) {
      val token = unsanitizedSource.peek()
      if (ignoredByParser(ignoreType, token.type)) {
        attribution.push(token)
        unsanitizedSource.next()
        continue
      }
      return token
    }
  }

  private fun <T: Node> within(block: () -> T): T {
    attribution.enter()
    return attribution.exit(block())
  }

  private fun ignoredByParser(ignoreType: IgnoreType, type: TokenType): Boolean = ignoreType.ignored(type)

  private enum class IgnoreType {
    ExpressionList, Default;

    fun ignored(type: TokenType): Boolean = when {
      type == TokenType.BlockComment -> true
      type == TokenType.LineComment -> true
      type == TokenType.Whitespace -> true
      type == TokenType.Semicolon && this == Default -> true
      type == TokenType.Line && this == Default -> true
      else -> false
    }
  }
}
