package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.*
import kotlin.math.abs

class EvaluationVisitor(root: Scope) : NodeVisitor<Any> {
  private var currentScope: Scope = root

  override fun visitIntegerLiteral(node: IntegerLiteral): Any = node.value
  override fun visitDoubleLiteral(node: DoubleLiteral): Any = node.value
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
    currentScope.define(node.symbol.id, value, ValueStoreType.Let)
    return value
  }

  override fun visitSymbolReference(node: SymbolReference): Any =
    currentScope.value(node.symbol.id)

  override fun visitVarAssignment(node: VarAssignment): Any {
    val value = node.value.visit(this)
    currentScope.define(node.symbol.id, value, type = ValueStoreType.Var)
    return value
  }

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

  override fun visitSetAssignment(node: SetAssignment): Any {
    val value = node.value.visit(this)
    currentScope.set(node.symbol.id, value)
    return value
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

    if (left is Double || right is Double) {
      return numericOperation(
        node.op,
        left,
        right,
        convert = { it.toDouble() },
        add = { a, b -> a + b },
        subtract = { a, b -> a - b },
        multiply = { a, b -> a * b },
        divide = { a, b -> a / b },
        euclideanModulo = { _, _ -> throw RuntimeException("Can't perform integer modulo between floating point types") },
        remainder = { _, _ -> throw RuntimeException("Can't perform integer remainder between floating point types") },
        lesser = { a, b -> a < b },
        greater = { a, b -> a > b },
        lesserEqual = { a, b -> a <= b },
        greaterEqual = { a, b -> a >= b }
      )
    }

    if (left is Float || right is Float) {
      return numericOperation(
        node.op,
        left,
        right,
        convert = { it.toFloat() },
        add = { a, b -> a + b },
        subtract = { a, b -> a - b },
        multiply = { a, b -> a * b },
        divide = { a, b -> a / b },
        euclideanModulo = { _, _ -> throw RuntimeException("Can't perform integer modulo between floating point types") },
        remainder = { _, _ -> throw RuntimeException("Can't perform integer remainder between floating point types") },
        lesser = { a, b -> a < b },
        greater = { a, b -> a > b },
        lesserEqual = { a, b -> a <= b },
        greaterEqual = { a, b -> a >= b }
      )
    }

    if (left is Long || right is Long) {
      return numericOperation(
        node.op,
        left,
        right,
        convert = { it.toLong() },
        add = { a, b -> a + b },
        subtract = { a, b -> a - b },
        multiply = { a, b -> a * b },
        divide = { a, b -> a / b },
        euclideanModulo = { x, d -> (x % d).let { q -> if (q < 0) q + abs(d) else q } },
        remainder = { x, d -> x % d },
        lesser = { a, b -> a < b },
        greater = { a, b -> a > b },
        lesserEqual = { a, b -> a <= b },
        greaterEqual = { a, b -> a >= b }
      )
    }

    if (left is Int || right is Int) {
      return numericOperation(
        node.op,
        left,
        right,
        convert = { it.toInt() },
        add = { a, b -> a + b },
        subtract = { a, b -> a - b },
        multiply = { a, b -> a * b },
        divide = { a, b -> a / b },
        euclideanModulo = { x, d -> (x % d).let { q -> if (q < 0) q + abs(d) else q } },
        remainder = { x, d -> x % d },
        lesser = { a, b -> a < b },
        greater = { a, b -> a > b },
        lesserEqual = { a, b -> a <= b },
        greaterEqual = { a, b -> a >= b }
      )
    }

    throw RuntimeException("Unknown numeric type: ${left.javaClass.name}")
  }

  private inline fun <T: Number> numericOperation(
    op: InfixOperator,
    left: Number,
    right: Number,
    convert: (Number) -> T,
    add: (T, T) -> T,
    subtract: (T, T) -> T,
    multiply: (T, T) -> T,
    divide: (T, T) -> T,
    euclideanModulo: (T, T) -> T,
    remainder: (T, T) -> T,
    lesser: (T, T) -> Boolean,
    greater: (T, T) -> Boolean,
    lesserEqual: (T, T) -> Boolean,
    greaterEqual: (T, T) -> Boolean
  ): Any {
    return when (op) {
      InfixOperator.Plus -> add(convert(left), convert(right))
      InfixOperator.Minus -> subtract(convert(left), convert(right))
      InfixOperator.Multiply -> multiply(convert(left), convert(right))
      InfixOperator.Divide -> divide(convert(left), convert(right))
      InfixOperator.EuclideanModulo -> euclideanModulo(convert(left), convert(right))
      InfixOperator.Remainder -> remainder(convert(left), convert(right))
      InfixOperator.Lesser -> lesser(convert(left), convert(right))
      InfixOperator.Greater -> greater(convert(left), convert(right))
      InfixOperator.LesserEqual -> lesserEqual(convert(left), convert(right))
      InfixOperator.GreaterEqual -> greaterEqual(convert(left), convert(right))
      else -> throw RuntimeException("Unable to handle operation $op")
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
