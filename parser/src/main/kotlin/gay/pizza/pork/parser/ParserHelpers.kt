package gay.pizza.pork.parser

import gay.pizza.pork.ast.InfixOperator
import gay.pizza.pork.ast.PrefixOperator
import gay.pizza.pork.ast.SuffixOperator

internal object ParserHelpers {
  fun convertInfixOperator(token: Token): InfixOperator = when (token.type) {
    TokenType.Plus -> InfixOperator.Plus
    TokenType.Minus -> InfixOperator.Minus
    TokenType.Multiply -> InfixOperator.Multiply
    TokenType.Divide -> InfixOperator.Divide
    TokenType.Ampersand -> InfixOperator.BinaryAnd
    TokenType.Pipe -> InfixOperator.BinaryOr
    TokenType.Caret -> InfixOperator.BinaryExclusiveOr
    TokenType.Equality -> InfixOperator.Equals
    TokenType.Inequality -> InfixOperator.NotEquals
    TokenType.Mod -> InfixOperator.EuclideanModulo
    TokenType.Rem -> InfixOperator.Remainder
    TokenType.Lesser -> InfixOperator.Lesser
    TokenType.Greater -> InfixOperator.Greater
    TokenType.LesserEqual -> InfixOperator.LesserEqual
    TokenType.GreaterEqual -> InfixOperator.GreaterEqual
    TokenType.And -> InfixOperator.BooleanAnd
    TokenType.Or -> InfixOperator.BooleanOr
    else -> throw ParserBase.ParseError("Unknown Infix Operator")
  }

  fun convertPrefixOperator(token: Token): PrefixOperator = when (token.type) {
    TokenType.Not -> PrefixOperator.BooleanNot
    TokenType.Plus -> PrefixOperator.UnaryPlus
    TokenType.Minus -> PrefixOperator.UnaryMinus
    TokenType.Tilde -> PrefixOperator.BinaryNot
    else -> throw ParserBase.ParseError("Unknown Prefix Operator")
  }

  fun convertSuffixOperator(token: Token): SuffixOperator = when (token.type) {
    TokenType.PlusPlus -> SuffixOperator.Increment
    TokenType.MinusMinus -> SuffixOperator.Decrement
    else -> throw ParserBase.ParseError("Unknown Suffix Operator")
  }
}
