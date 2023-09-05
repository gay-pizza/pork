package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.*

class EvaluationVisitor(root: Scope) : NodeVisitor<Any> {
  private var currentScope: Scope = root

  override fun visitIntLiteral(node: IntLiteral): Any = node.value
  override fun visitStringLiteral(node: StringLiteral): Any = node.text
  override fun visitBooleanLiteral(node: BooleanLiteral): Any = node.value
  override fun visitListLiteral(node: ListLiteral): Any =
    node.items.map { it.visit(this) }

  override fun visitSymbol(node: Symbol): Any = None

  override fun visitFunctionCall(node: FunctionCall): Any {
    val arguments = node.arguments.map { it.visit(this) }
    return currentScope.call(node.symbol.id, Arguments(arguments))
  }

  override fun visitLetAssignment(node: LetAssignment): Any {
    val value = node.value.visit(this)
    currentScope.define(node.symbol.id, value)
    return value
  }

  override fun visitSymbolReference(node: SymbolReference): Any =
    currentScope.value(node.symbol.id)

  override fun visitLambda(node: Lambda): CallableFunction {
    return CallableFunction { arguments ->
      currentScope = currentScope.fork()
      for ((index, argumentSymbol) in node.arguments.withIndex()) {
        currentScope.define(argumentSymbol.id, arguments.values[index])
      }
      try {
        var value: Any? = null
        for (expression in node.expressions) {
          value = expression.visit(this)
        }
        value ?: None
      } finally {
        currentScope = currentScope.leave()
      }
    }
  }

  override fun visitParentheses(node: Parentheses): Any =
    node.expression.visit(this)

  override fun visitPrefixOperation(node: PrefixOperation): Any {
    val value = node.expression.visit(this)
    return when (node.op) {
      PrefixOperator.Negate -> {
        if (value !is Boolean) {
          throw RuntimeException("Cannot negate a value which is not a boolean.")
        }
        !value
      }
    }
  }

  override fun visitIf(node: If): Any {
    val condition = node.condition.visit(this)
    return if (condition == true) {
      node.thenExpression.visit(this)
    } else {
      val elseExpression = node.elseExpression
      elseExpression?.visit(this) ?: None
    }
  }

  override fun visitInfixOperation(node: InfixOperation): Any {
    val left = node.left.visit(this)
    val right = node.right.visit(this)

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

  override fun visitBlock(node: Block): BlockFunction = BlockFunction {
    var value: Any? = null
    for (expression in node.expressions) {
      value = expression.visit(this)
    }
    value ?: None
  }

  override fun visitFunctionDefinition(node: FunctionDefinition): Any {
    throw RuntimeException(
      "Function declarations cannot be visited in an EvaluationVisitor. " +
      "Utilize a FunctionContext."
    )
  }

  override fun visitImportDeclaration(node: ImportDeclaration): Any {
    throw RuntimeException(
      "Import declarations cannot be visited in an EvaluationVisitor. " +
      "Utilize an EvaluationContext."
    )
  }

  override fun visitCompilationUnit(node: CompilationUnit): Any {
    throw RuntimeException(
      "Compilation units cannot be visited in an EvaluationVisitor. " +
        "Utilize an EvaluationContext."
    )
  }
}
