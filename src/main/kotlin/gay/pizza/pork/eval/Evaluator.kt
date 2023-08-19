package gay.pizza.pork.eval

import gay.pizza.pork.ast.*

class Evaluator(root: Scope) : Visitor<Any> {
  private var currentScope: Scope = root

  override fun visitDefine(node: Define): Any {
    val value = visit(node.value)
    currentScope.define(node.symbol.id, value)
    return value
  }

  override fun visitFunctionCall(node: FunctionCall): Any = currentScope.call(node.symbol.id)

  override fun visitReference(node: SymbolReference): Any =
    currentScope.value(node.symbol.id)

  override fun visitSymbol(node: Symbol): Any {
    return Unit
  }

  override fun visitLambda(node: Lambda): CallableFunction {
    return CallableFunction { _ ->
      currentScope = currentScope.fork()
      try {
        var value: Any? = null
        for (expression in node.expressions) {
          value = visit(expression)
        }
        value ?: Unit
      } finally {
        currentScope = currentScope.leave()
      }
    }
  }

  override fun visitIntLiteral(node: IntLiteral): Any = node.value
  override fun visitBooleanLiteral(node: BooleanLiteral): Any = node.value
  override fun visitListLiteral(node: ListLiteral): Any = node.items.map { visit(it) }

  override fun visitParentheses(node: Parentheses): Any = visit(node.expression)

  override fun visitInfixOperation(node: InfixOperation): Any {
    val left = visit(node.left)
    val right = visit(node.right)

    if (left !is Number || right !is Number) {
      throw RuntimeException("Failed to evaluate infix operation, bad types.")
    }

    val leftInt = left.toInt()
    val rightInt = right.toInt()

    return when (node.op) {
      InfixOperator.Plus -> leftInt + rightInt
      InfixOperator.Minus -> leftInt - rightInt
      InfixOperator.Multiply -> leftInt * rightInt
      InfixOperator.Divide -> leftInt / rightInt
    }
  }

  override fun visitProgram(node: Program): Any {
    var value: Any? = null
    for (expression in node.expressions) {
      value = visit(expression)
    }
    return value ?: Unit
  }
}
