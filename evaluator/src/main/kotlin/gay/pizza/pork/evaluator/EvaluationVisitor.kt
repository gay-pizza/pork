package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.*

class EvaluationVisitor(root: Scope) : NodeVisitor<Any> {
  private var currentScope: Scope = root

  override fun visitIntLiteral(node: IntLiteral): Any = node.value
  override fun visitStringLiteral(node: StringLiteral): Any = node.text
  override fun visitBooleanLiteral(node: BooleanLiteral): Any = node.value
  override fun visitListLiteral(node: ListLiteral): Any = node.items.map { visit(it) }

  override fun visitSymbol(node: Symbol): Any = None

  override fun visitFunctionCall(node: FunctionCall): Any {
    val arguments = node.arguments.map { visit(it) }
    return currentScope.call(node.symbol.id, Arguments(arguments))
  }

  override fun visitLetAssignment(node: LetAssignment): Any {
    val value = visit(node.value)
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
          value = visit(expression)
        }
        value ?: None
      } finally {
        currentScope = currentScope.leave()
      }
    }
  }

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

  override fun visitIf(node: If): Any {
    val condition = visit(node.condition)
    return if (condition == true) {
      visit(node.thenExpression)
    } else {
      if (node.elseExpression != null) {
        visit(node.elseExpression!!)
      } else {
        None
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

  override fun visitFunctionDefinition(node: FunctionDefinition): Any {
    val blockFunction = visitBlock(node.block) as BlockFunction
    val function = CallableFunction { arguments ->
      currentScope = currentScope.fork(inheritFastCache = true)
      currentScope.fastVariableCache.put(node.symbol.id, currentScope.value(node.symbol.id))
      for ((index, argumentSymbol) in node.arguments.withIndex()) {
        currentScope.define(argumentSymbol.id, arguments.values[index])
      }
      try {
        return@CallableFunction blockFunction.call()
      } finally {
        currentScope = currentScope.leave()
      }
    }
    currentScope.define(node.symbol.id, function)
    return None
  }

  override fun visitBlock(node: Block): Any = BlockFunction {
    var value: Any? = null
    for (expression in node.expressions) {
      value = visit(expression)
    }
    value ?: None
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
