package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.*

class EvaluationVisitor(root: Scope) : NodeVisitor<Any> {
  private var currentScope: Scope = root

  override fun visitIntLiteral(node: IntLiteral): Any = node.value
  override fun visitStringLiteral(node: StringLiteral): Any = node.text
  override fun visitBooleanLiteral(node: BooleanLiteral): Any = node.value

  override fun visitBreak(node: Break): Any = throw BreakMarker

  override fun visitListLiteral(node: ListLiteral): Any =
    node.items.map { it.visit(this) }

  override fun visitSymbol(node: Symbol): Any = None

  override fun visitFunctionCall(node: FunctionCall): Any {
    val arguments = node.arguments.map { it.visit(this) }
    val functionValue = currentScope.value(node.symbol.id) as CallableFunction
    return functionValue.call(Arguments(arguments))
  }

  override fun visitLetAssignment(node: LetAssignment): Any {
    val value = node.value.visit(this)
    currentScope.define(node.symbol.id, value)
    return value
  }

  override fun visitSymbolReference(node: SymbolReference): Any =
    currentScope.value(node.symbol.id)

  override fun visitWhile(node: While): Any {
    val blockFunction = node.block.visit(this) as BlockFunction
    var result: Any? = null
    while (true) {
      val value = node.condition.visit(this)
      if (value !is Boolean) {
        throw RuntimeException("While loop attempted on non-boolean value: $value")
      }
      if (!value) break
      try {
        scoped { result = blockFunction.call() }
      } catch (_: BreakMarker) {
        break
      } catch (_: ContinueMarker) {
        continue
      }
    }
    return result ?: None
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
      val blockFunction = node.thenBlock.visit(this) as BlockFunction
      scoped { blockFunction.call() }
    } else if (node.elseBlock != null) {
      val blockFunction = node.elseBlock!!.visit(this) as BlockFunction
      scoped { blockFunction.call() }
    } else None
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
        "Utilize an CompilationUnitContext."
    )
  }

  override fun visitCompilationUnit(node: CompilationUnit): Any {
    throw RuntimeException(
      "Compilation units cannot be visited in an EvaluationVisitor. " +
        "Utilize an CompilationUnitContext."
    )
  }

  override fun visitNative(node: Native): Any {
    throw RuntimeException(
      "Native definition cannot be visited in an EvaluationVisitor. " +
        "Utilize an FunctionContext."
    )
  }

  override fun visitContinue(node: Continue): Any = ContinueMarker

  private inline fun <T> scoped(block: () -> T): T {
    currentScope = currentScope.fork()
    try {
      return block()
    } finally {
      currentScope = currentScope.leave()
    }
  }

  private object BreakMarker : RuntimeException("Break Marker")
  private object ContinueMarker: RuntimeException("Continue Marker")
}
