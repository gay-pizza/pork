package gay.pizza.pork.eval

import gay.pizza.pork.ast.*
import gay.pizza.pork.ast.nodes.*

class Evaluator(root: Scope) : NodeVisitor<Any> {
  private var currentScope: Scope = root

  override fun visitDefine(node: Define): Any {
    val value = visit(node.value)
    currentScope.define(node.symbol.id, value)
    return value
  }

  override fun visitFunctionCall(node: FunctionCall): Any {
    val arguments = node.arguments.map { visit(it) }
    return currentScope.call(node.symbol.id, Arguments(arguments))
  }

  override fun visitReference(node: SymbolReference): Any =
    currentScope.value(node.symbol.id)

  override fun visitIf(node: If): Any {
    val condition = visit(node.condition)
    return if (condition == true) {
      visit(node.thenExpression)
    } else {
      if (node.elseExpression != null) {
        visit(node.elseExpression)
      } else {
        None
      }
    }
  }

  override fun visitSymbol(node: Symbol): Any {
    return None
  }

  override fun visitLambda(node: Lambda): CallableFunction {
    return CallableFunction { arguments ->
      currentScope = currentScope.fork()
      for ((index, argumentSymbol) in node.arguments.withIndex()) {
        currentScope.define(argumentSymbol.id, arguments.values[index])
      }
      try {
        var value: Any? = null
        for (expression in node.expressions) {
          value = visit(expression)
        }
        value ?: None
      } finally {
        currentScope = currentScope.leave()
      }
    }
  }

  override fun visitIntLiteral(node: IntLiteral): Any = node.value
  override fun visitBooleanLiteral(node: BooleanLiteral): Any = node.value
  override fun visitListLiteral(node: ListLiteral): Any = node.items.map { visit(it) }
  override fun visitStringLiteral(node: StringLiteral): Any = node.text

  override fun visitParentheses(node: Parentheses): Any = visit(node.expression)

  override fun visitPrefixOperation(node: PrefixOperation): Any {
    val value = visit(node.expression)
    return when (node.op) {
      PrefixOperator.Negate -> {
        if (value !is Boolean) {
          throw RuntimeException("Cannot negate a value which is not a boolean.")
        }
        !value
      }
    }
  }

  override fun visitInfixOperation(node: InfixOperation): Any {
    val left = visit(node.left)
    val right = visit(node.right)

    when (node.op) {
      InfixOperator.Equals -> {
        return left == right
      }
      InfixOperator.NotEquals -> {
        return left != right
      }
      else -> {}
    }

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
      else -> throw RuntimeException("Unable to handle operation ${node.op}")
    }
  }

  override fun visitProgram(node: Program): Any {
    var value: Any? = null
    for (expression in node.expressions) {
      value = visit(expression)
    }
    return value ?: None
  }
}
