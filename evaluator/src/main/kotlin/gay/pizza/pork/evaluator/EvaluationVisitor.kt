package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.FunctionLevelVisitor
import gay.pizza.pork.ast.gen.*
import gay.pizza.pork.execution.None
import kotlin.math.abs

@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
class EvaluationVisitor(root: Scope, val stack: CallStack) : FunctionLevelVisitor<Any>() {
  private var currentScope: Scope = root

  override fun visitIntegerLiteral(node: IntegerLiteral): Any = node.value
  override fun visitDoubleLiteral(node: DoubleLiteral): Any = node.value
  override fun visitForIn(node: ForIn): Any {
    val blockFunction = node.block.visit(this) as BlockFunction
    var result: Any? = null
    val value = node.expression.visit(this)
    if (value !is Iterable<*>) {
      throw RuntimeException("Unable to iterate on value that is not a iterable.")
    }

    var reuseScope: Scope? = null

    for (item in value) {
      try {
        if (reuseScope == null) {
          reuseScope = currentScope.fork(name = "ForIn")
        }

        scoped(reuseScope, node = node) {
          currentScope.define(node.item.symbol.id, item ?: None)
          result = blockFunction.call(false)
        }
      } catch (_: BreakMarker) {
        break
      } catch (_: ContinueMarker) {
        continue
      }
    }
    reuseScope?.disown()
    return result ?: None
  }

  override fun visitForInItem(node: ForInItem): Any =
    throw RuntimeException("Visiting ForInItem is not supported.")

  override fun visitStringLiteral(node: StringLiteral): Any = node.text
  override fun visitBooleanLiteral(node: BooleanLiteral): Any = node.value

  override fun visitBreak(node: Break): Any = throw BreakMarker

  override fun visitListLiteral(node: ListLiteral): Any =
    node.items.map { it.visit(this) }

  override fun visitLongLiteral(node: LongLiteral): Any = node.value

  override fun visitFunctionCall(node: FunctionCall): Any {
    val arguments = node.arguments.map { it.visit(this) }
    val functionValue = currentScope.value(node.symbol.id) as CallableFunction
    return functionValue.call(arguments, stack)
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
    var reuseScope: Scope? = null
    while (true) {
      val value = node.condition.visit(this)
      if (value !is Boolean) {
        throw RuntimeException("While loop attempted on non-boolean value: $value")
      }
      if (!value) break
      try {
        if (reuseScope == null) {
          reuseScope = currentScope.fork(name = "While")
        }
        scoped(reuseScope, node = node) { result = blockFunction.call(false) }
      } catch (_: BreakMarker) {
        break
      } catch (_: ContinueMarker) {
        continue
      }
    }
    reuseScope?.disown()
    return result ?: None
  }

  override fun visitParentheses(node: Parentheses): Any =
    node.expression.visit(this)

  override fun visitPrefixOperation(node: PrefixOperation): Any {
    val value = node.expression.visit(this)
    return when (node.op) {
      PrefixOperator.BooleanNot -> {
        if (value !is Boolean) {
          throw RuntimeException("Cannot negate a value which is not a boolean.")
        }
        !value
      }
      PrefixOperator.UnaryPlus, PrefixOperator.UnaryMinus, PrefixOperator.BinaryNot -> {
        if (value !is Number) {
          throw RuntimeException("Numeric unary '${node.op.token}' illegal on non-numeric type")
        }
        unaryNumericOperation(node, value)
      }
    }
  }

  override fun visitReturn(node: Return): Any = ReturnValue(node.value.visit(this))

  private fun unaryNumericOperation(node: PrefixOperation, value: Number) = when (value) {
    is Double -> {
      unaryNumericOperation(
        node.op,
        value,
        convert = { it.toDouble() },
        plus = { +it },
        minus = { -it },
        binaryNot = { unaryFloatingPointTypeError("binary not") }
      )
    }
    is Float -> {
      unaryNumericOperation(
        node.op,
        value,
        convert = { it.toFloat() },
        plus = { +it },
        minus = { -it },
        binaryNot = { unaryFloatingPointTypeError("binary not") }
      )
    }
    is Long -> {
      unaryNumericOperation(
        node.op,
        value,
        convert = { it.toLong() },
        plus = { +it },
        minus = { -it },
        binaryNot = { it.inv() }
      )
    }
    is Int -> {
      unaryNumericOperation(
        node.op,
        value,
        convert = { it.toInt() },
        plus = { +it },
        minus = { -it },
        binaryNot = { it.inv() }
      )
    }
    else -> throw RuntimeException("Unknown numeric type")
  }

  override fun visitSuffixOperation(node: SuffixOperation): Any {
    val previousValue = currentScope.value(node.reference.symbol.id)
    val infix = visitInfixOperation(InfixOperation(node.reference, when (node.op) {
      SuffixOperator.Increment -> InfixOperator.Plus
      SuffixOperator.Decrement -> InfixOperator.Minus
    }, IntegerLiteral(1)))
    currentScope.set(node.reference.symbol.id, infix)
    return previousValue
  }

  override fun visitSymbolSetAssignment(node: SymbolSetAssignment): Any {
    val value = node.value.visit(this)
    currentScope.set(node.symbol.id, value)
    return value
  }

  override fun visitIf(node: If): Any {
    val condition = node.condition.visit(this)
    return if (condition == true) {
      val blockFunction = node.thenBlock.visit(this) as BlockFunction
      scoped(node = node) { blockFunction.call(false) }
    } else if (node.elseBlock != null) {
      val blockFunction = node.elseBlock!!.visit(this) as BlockFunction
      scoped(node = node) { blockFunction.call(false) }
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

    if (left is Boolean && right is Boolean) {
      when (node.op) {
        InfixOperator.BooleanAnd -> return left && right
        InfixOperator.BooleanOr -> return left || right
        else -> {}
      }
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
        binaryAnd = { _, _ -> floatingPointTypeError("binary and") },
        binaryOr = { _, _ -> floatingPointTypeError("binary or") },
        binaryExclusiveOr = { _, _ -> floatingPointTypeError("binary exclusive-or") },
        euclideanModulo = { _, _ -> floatingPointTypeError("integer modulo") },
        remainder = { _, _ -> floatingPointTypeError("integer remainder") },
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
        binaryAnd = { _, _ -> floatingPointTypeError("binary and") },
        binaryOr = { _, _ -> floatingPointTypeError("binary or") },
        binaryExclusiveOr = { _, _ -> floatingPointTypeError("binary exclusive-or") },
        euclideanModulo = { _, _ -> floatingPointTypeError("integer modulo") },
        remainder = { _, _ -> floatingPointTypeError("integer remainder") },
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
        binaryAnd = { a, b -> a and b },
        binaryOr = { a, b -> a or b },
        binaryExclusiveOr = { a, b -> a xor b },
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
        binaryAnd = { a, b -> a and b },
        binaryOr = { a, b -> a or b },
        binaryExclusiveOr = { a, b -> a xor b },
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
    binaryAnd: (T, T) -> T,
    binaryOr: (T, T) -> T,
    binaryExclusiveOr: (T, T) -> T,
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
      InfixOperator.BinaryAnd -> binaryAnd(convert(left), convert(right))
      InfixOperator.BinaryOr -> binaryOr(convert(left), convert(right))
      InfixOperator.BinaryExclusiveOr -> binaryExclusiveOr(convert(left), convert(right))
      InfixOperator.EuclideanModulo -> euclideanModulo(convert(left), convert(right))
      InfixOperator.Remainder -> remainder(convert(left), convert(right))
      InfixOperator.Lesser -> lesser(convert(left), convert(right))
      InfixOperator.Greater -> greater(convert(left), convert(right))
      InfixOperator.LesserEqual -> lesserEqual(convert(left), convert(right))
      InfixOperator.GreaterEqual -> greaterEqual(convert(left), convert(right))
      InfixOperator.Equals, InfixOperator.NotEquals, InfixOperator.BooleanAnd, InfixOperator.BooleanOr ->
        throw RuntimeException("Unable to handle operation $op")
    }
  }

  private inline fun <T: Number> unaryNumericOperation(
    op: PrefixOperator,
    value: Number,
    convert: (Number) -> T,
    plus: (T) -> T,
    minus: (T) -> T,
    binaryNot: (T) -> T
  ): Any {
    return when (op) {
      PrefixOperator.BooleanNot -> throw RuntimeException("Unable to handle operation $op")
      PrefixOperator.UnaryPlus -> plus(convert(value))
      PrefixOperator.UnaryMinus -> minus(convert(value))
      PrefixOperator.BinaryNot -> binaryNot(convert(value))
    }
  }

  override fun visitArgumentSpec(node: ArgumentSpec): Any =
    throw RuntimeException("Visiting ArgumentSpec is not supported.")

  override fun visitBlock(node: Block): BlockFunction {
    val visitor = this
    return object : BlockFunction() {
      override fun call(isFunctionContext: Boolean): Any {
        var value: Any? = null
        for (expression in node.expressions) {
          value = expression.visit(visitor)
          if (isFunctionContext && value is ReturnValue) {
            return value.value
          }
        }
        return value ?: None
      }
    }
  }

  override fun visitIndexedBy(node: IndexedBy): Any {
    val value = node.expression.visit(this)
    val index = node.index.visit(this)

    if (value is List<*> && index is Number) {
      return value[index.toInt()] ?: None
    }

    if (value is Array<*> && index is Number) {
      return value[index.toInt()] ?: None
    }

    throw RuntimeException("Failed to index '${value}' by '${index}': Unsupported types used.")
  }

  override fun visitIndexedSetAssignment(node: IndexedSetAssignment): Any {
    val value = node.value.visit(this)
    val index = node.index.visit(this)
    val target = node.target.visit(this)
    if (target is MutableList<*> && index is Number) {
      @Suppress("UNCHECKED_CAST")
      (target as MutableList<Any?>)[index.toInt()] = value
      return None
    }

    if (target is Array<*> && index is Number) {
      @Suppress("UNCHECKED_CAST")
      (target as MutableList<Any?>)[index.toInt()] = value
      return None
    }
    return value
  }

  override fun visitNoneLiteral(node: NoneLiteral): Any = None

  override fun visitContinue(node: Continue): Any = ContinueMarker

  private inline fun <T> scoped(reuseScope: Scope? = null, node: Node? = null, block: () -> T): T {
    val previousScope = currentScope
    currentScope = reuseScope ?: currentScope.fork(name = node?.type?.name)
    try {
      return block()
    } finally {
      if (reuseScope == null) {
        currentScope = currentScope.leave(disown = true)
      } else {
        reuseScope.markForReuse()
        currentScope = previousScope
      }
    }
  }

  private fun unaryFloatingPointTypeError(operation: String): Nothing {
    throw RuntimeException("Can't perform $operation on a floating point type")
  }

  private fun floatingPointTypeError(operation: String): Nothing {
    throw RuntimeException("Can't perform $operation between floating point types")
  }

  private object BreakMarker : RuntimeException("Break Marker")
  private object ContinueMarker: RuntimeException("Continue Marker")
}
