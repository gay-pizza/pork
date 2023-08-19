package gay.pizza.pork.parse

import gay.pizza.pork.ast.*

class PorkParser(val source: PeekableSource<Token>) {
  private fun readIntLiteral(): IntLiteral {
    val token = expect(TokenType.IntLiteral)
    return IntLiteral(token.text.toInt())
  }

  private fun readSymbol(): Symbol {
    val token = expect(TokenType.Symbol)
    return Symbol(token.text)
  }

  private fun readSymbolCases(): Expression {
    val symbol = readSymbol()
    return if (peekType(TokenType.LeftParentheses)) {
      expect(TokenType.LeftParentheses)
      expect(TokenType.RightParentheses)
      FunctionCall(symbol)
    } else if (peekType(TokenType.Equals)) {
      expect(TokenType.Equals)
      Define(symbol, readExpression())
    } else {
      SymbolReference(symbol)
    }
  }

  fun readLambda(): Lambda {
    expect(TokenType.LeftCurly)
    val items = collectExpressions(TokenType.RightCurly)
    expect(TokenType.RightCurly)
    return Lambda(items)
  }

  fun readExpression(): Expression {
    val token = source.peek()
    val expression = when (token.type) {
      TokenType.IntLiteral -> {
        readIntLiteral()
      }
      TokenType.LeftBracket -> {
        readListLiteral()
      }
      TokenType.Symbol -> {
        readSymbolCases()
      }
      TokenType.LeftCurly -> {
        readLambda()
      }
      TokenType.LeftParentheses -> {
        expect(TokenType.LeftParentheses)
        val expression = readExpression()
        expect(TokenType.RightParentheses)
        Parentheses(expression)
      }
      TokenType.True -> {
        expect(TokenType.True)
        return BooleanLiteral(true)
      }
      TokenType.False -> {
        expect(TokenType.False)
        return BooleanLiteral(false)
      }
      else -> {
        throw RuntimeException("Failed to parse token: ${token.type} '${token.text}' as expression.")
      }
    }

    if (peekType(TokenType.Plus, TokenType.Minus, TokenType.Multiply, TokenType.Divide)) {
      val infixToken = source.next()
      val infixOperator = convertInfixOperator(infixToken)
      return InfixOperation(expression, infixOperator, readExpression())
    }
    return expression
  }

  private fun convertInfixOperator(token: Token): InfixOperator =
    when (token.type) {
      TokenType.Plus -> InfixOperator.Plus
      TokenType.Minus -> InfixOperator.Minus
      TokenType.Multiply -> InfixOperator.Multiply
      TokenType.Divide -> InfixOperator.Divide
      else -> throw RuntimeException("Unknown Infix Operator")
    }

  fun readListLiteral(): ListLiteral {
    expect(TokenType.LeftBracket)
    val items = collectExpressions(TokenType.RightBracket, TokenType.Comma)
    expect(TokenType.RightBracket)
    return ListLiteral(items)
  }

  fun readProgram(): Program {
    val items = collectExpressions(TokenType.EndOfFile)
    expect(TokenType.EndOfFile)
    return Program(items)
  }

  private fun collectExpressions(peeking: TokenType, consuming: TokenType? = null): List<Expression> {
    val items = mutableListOf<Expression>()
    while (!peekType(peeking)) {
      val expression = readExpression()
      if (consuming != null && !peekType(peeking)) {
        expect(consuming)
      }
      items.add(expression)
    }
    return items
  }

  private fun peekType(vararg types: TokenType): Boolean {
    val token = source.peek()
    return types.contains(token.type)
  }

  private fun expect(type: TokenType): Token {
    val token = source.next()
    if (token.type != type) {
      throw RuntimeException("Expected token type '${type}' but got type ${token.type} '${token.text}'")
    }
    return token
  }
}
