package gay.pizza.pork.compiler

import gay.pizza.pork.ast.FunctionLevelVisitor
import gay.pizza.pork.ast.gen.*
import gay.pizza.pork.bytecode.MutableRel
import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode

class StubOpEmitter(val compiler: Compiler, val symbol: CompilableSymbol) : FunctionLevelVisitor<Unit>() {
  private val ops = mutableListOf<StubOp>()

  private var loopState: LoopState? = null
  private val localVariables = mutableListOf<MutableList<StubVar>>()
  private var localVarIndex = 0u

  private val requiredLoopState: LoopState
    get() {
      if (loopState != null) {
        return loopState!!
      }
      throw RuntimeException("loopState expected but was not found.")
    }

  private fun allocateLocalVariable(symbol: Symbol): StubVar {
    val scope = localVariables.last()
    val variable = StubVar(localVarIndex++, symbol)
    scope.add(variable)
    return variable
  }

  private fun resolveSymbol(symbol: Symbol): CallOrStubVar {
    for (scope in localVariables.reversed()) {
      val found = scope.firstOrNull { it.symbol == symbol }
      if (found != null) {
        return CallOrStubVar(stubVar = found)
      }
    }
    val found = this.symbol.compilableSlab.compilableSymbolOf(symbol)
    if (found != null) {
      return CallOrStubVar(call = found)
    }
    throw RuntimeException("Unable to resolve symbol: ${symbol.id}")
  }

  private fun pushScope() {
    emit(Opcode.ScopeIn)
    localVariables.add(mutableListOf())
  }

  private fun popScope() {
    emit(Opcode.ScopeOut)
    localVariables.removeLast()
  }

  fun enter() {
    pushScope()
  }

  fun allocateOuterScope(definition: FunctionDefinition) {
    val allNormalArguments = definition.arguments.takeWhile { !it.multiple }
    val varArgument = definition.arguments.firstOrNull { it.multiple }

    for (arg in allNormalArguments.reversed()) {
      val functionLocal = allocateLocalVariable(arg.symbol)
      emit(Opcode.StoreLocal, listOf(functionLocal.index))
    }

    if (varArgument != null) {
      val functionLocal = allocateLocalVariable(varArgument.symbol)
      emit(Opcode.StoreLocal, listOf(functionLocal.index))
    }
  }

  fun exit() {
    popScope()
    emit(Opcode.Return)
  }

  override fun visitBlock(node: Block) {
    pushScope()
    node.visitChildren(this)
    popScope()
  }

  override fun visitBooleanLiteral(node: BooleanLiteral) {
    emit(if (node.value) Opcode.True else Opcode.False)
  }

  override fun visitBreak(node: Break) {
    patch(Opcode.Jump, listOf(0u), 0, symbol, requiredLoopState.exitJumpTarget)
  }

  override fun visitContinue(node: Continue) {
    patch(Opcode.Jump, listOf(0u), 0, symbol, requiredLoopState.startOfLoop)
  }

  override fun visitDoubleLiteral(node: DoubleLiteral) {
    emit(Opcode.Integer, listOf(node.value.toUInt()))
  }

  override fun visitForIn(node: ForIn) {
    TODO("ForIn is currently unsupported")
  }

  override fun visitFunctionCall(node: FunctionCall) {
    val targetScopeSymbol = symbol.scopeSymbol.scope.resolve(node.symbol) ?:
      throw RuntimeException("Unable to resolve symbol: ${node.symbol.id}")
    val targetSymbol = compiler.resolve(targetScopeSymbol)
    val functionDefinition = targetSymbol.scopeSymbol.definition as FunctionDefinition
    val retRel = MutableRel(0u)
    patch(Opcode.Integer, listOf(0u), 0, symbol, retRel)

    val normalArguments = mutableListOf<Expression>()
    var variableArguments: List<Expression>? = null
    for ((index, item) in functionDefinition.arguments.zip(node.arguments).withIndex()) {
      val (spec, value) = item
      if (spec.multiple) {
        val remaining = node.arguments.drop(index)
        variableArguments = remaining
        break
      } else {
        normalArguments.add(value)
      }
    }

    if (variableArguments != null) {
      for (item in variableArguments.reversed()) {
        item.visit(this)
      }
      emit(Opcode.List, listOf(variableArguments.size.toUInt()))
    }

    for (item in normalArguments.reversed()) {
      visit(item)
    }

    retRel.rel = (ops.size + 1).toUInt()
    patch(Opcode.Call, listOf(0u), mapOf(0 to targetSymbol))
  }

  override fun visitIf(node: If) {
    val thenRel = MutableRel(0u)
    val endRel = MutableRel(0u)
    node.condition.visit(this)
    patch(Opcode.JumpIf, listOf(0u), 0, symbol, thenRel)
    node.elseBlock?.visit(this)
    patch(Opcode.Jump, listOf(0u), 0, symbol, endRel)
    thenRel.rel = ops.size.toUInt()
    node.thenBlock.visit(this)
    endRel.rel = ops.size.toUInt()
  }

  override fun visitIndexedBy(node: IndexedBy) {
    node.expression.visit(this)
    node.index.visit(this)
    emit(Opcode.Index)
  }

  override fun visitInfixOperation(node: InfixOperation) {
    node.left.visit(this)
    node.right.visit(this)
    when (node.op) {
      InfixOperator.Plus -> emit(Opcode.Add)
      InfixOperator.Minus -> emit(Opcode.Subtract)
      InfixOperator.Multiply -> emit(Opcode.Multiply)
      InfixOperator.Divide -> emit(Opcode.Divide)
      InfixOperator.Equals -> emit(Opcode.CompareEqual)
      InfixOperator.NotEquals -> {
        emit(Opcode.CompareEqual)
        emit(Opcode.Not)
      }
      InfixOperator.EuclideanModulo -> emit(Opcode.EuclideanModulo)
      InfixOperator.Remainder -> emit(Opcode.Remainder)
      InfixOperator.Lesser -> emit(Opcode.CompareLesser)
      InfixOperator.Greater -> emit(Opcode.CompareGreater)
      InfixOperator.GreaterEqual -> emit(Opcode.CompareGreaterEqual)
      InfixOperator.LesserEqual -> emit(Opcode.CompareLesserEqual)
      InfixOperator.BooleanAnd -> emit(Opcode.And)
      InfixOperator.BooleanOr -> emit(Opcode.Or)
      InfixOperator.BinaryAnd -> emit(Opcode.BinaryAnd)
      InfixOperator.BinaryOr -> emit(Opcode.BinaryOr)
      InfixOperator.BinaryExclusiveOr -> emit(Opcode.BinaryXor)
    }
  }

  override fun visitIntegerLiteral(node: IntegerLiteral) {
    emit(Opcode.Integer, listOf(node.value.toUInt()))
  }

  override fun visitLetAssignment(node: LetAssignment) {
    val variable = allocateLocalVariable(node.symbol)
    node.value.visit(this)
    emit(Opcode.StoreLocal, listOf(variable.index))
  }

  override fun visitListLiteral(node: ListLiteral) {
    val count = node.items.size
    for (item in node.items) {
      item.visit(this)
    }
    emit(Opcode.List, listOf(count.toUInt()))
  }

  override fun visitLongLiteral(node: LongLiteral) {
    emit(Opcode.Integer, listOf(node.value.toUInt()))
  }

  override fun visitNoneLiteral(node: NoneLiteral) {
    emit(Opcode.None)
  }

  override fun visitParentheses(node: Parentheses) {
    node.expression.visit(this)
  }

  override fun visitPrefixOperation(node: PrefixOperation) {
    node.expression.visit(this)
    when (node.op) {
      PrefixOperator.BooleanNot -> emit(Opcode.Not)
      PrefixOperator.UnaryPlus -> emit(Opcode.UnaryPlus)
      PrefixOperator.UnaryMinus -> emit(Opcode.UnaryMinus)
      PrefixOperator.BinaryNot -> emit(Opcode.BinaryNot)
    }
  }

  override fun visitSetAssignment(node: SetAssignment) {
    val stubVarOrCall = resolveSymbol(node.symbol)
    if (stubVarOrCall.stubVar == null) {
      throw RuntimeException("Invalid set assignment.")
    }
    node.value.visit(this)
    emit(Opcode.StoreLocal, listOf(stubVarOrCall.stubVar.index))
  }

  override fun visitStringLiteral(node: StringLiteral) {
    val bytes = node.text.toByteArray()
    val constant = compiler.constantPool.assign(bytes)
    emit(Opcode.Constant, listOf(constant))
  }

  override fun visitSuffixOperation(node: SuffixOperation) {
    val stubVarOrCall = resolveSymbol(node.reference.symbol)
    if (stubVarOrCall.stubVar == null) {
      throw RuntimeException("Invalid suffix operation.")
    }
    load(stubVarOrCall)
    when (node.op) {
      SuffixOperator.Increment -> {
        emit(Opcode.Integer, listOf(1u))
        emit(Opcode.Add, emptyList())
        emit(Opcode.StoreLocal, listOf(stubVarOrCall.stubVar.index))
      }
      SuffixOperator.Decrement -> {
        emit(Opcode.Integer, listOf(1u))
        emit(Opcode.Subtract, emptyList())
        emit(Opcode.StoreLocal, listOf(stubVarOrCall.stubVar.index))
      }
    }
  }

  override fun visitSymbolReference(node: SymbolReference) {
    val variable = resolveSymbol(node.symbol)
    load(variable)
  }

  override fun visitVarAssignment(node: VarAssignment) {
    val variable = allocateLocalVariable(node.symbol)
    node.value.visit(this)
    emit(Opcode.StoreLocal, listOf(variable.index))
  }

  override fun visitWhile(node: While) {
    val startOfBody = MutableRel(0u)
    val endOfLoop = MutableRel(0u)
    val currentLoopState = LoopState(
      startOfLoop = ops.size.toUInt(),
      exitJumpTarget = endOfLoop,
      body = startOfBody,
      scopeDepth = (loopState?.scopeDepth ?: 0) + 1,
      enclosing = loopState
    )
    loopState = currentLoopState
    node.condition.visit(this)
    patch(Opcode.JumpIf, listOf(0u), 0, symbol, startOfBody)
    patch(Opcode.Jump, listOf(0u), 0, symbol, endOfLoop)
    startOfBody.rel = ops.size.toUInt()
    node.block.visit(this)
    patch(Opcode.Jump, listOf(0u), 0, symbol, currentLoopState.startOfLoop)
    endOfLoop.rel = ops.size.toUInt()
  }

  override fun visitNativeFunctionDescriptor(node: NativeFunctionDescriptor) {
    for (def in node.definitions) {
      val defConstant = compiler.constantPool.assign(def.text.toByteArray())
      emit(Opcode.Constant, listOf(defConstant))
    }
    val formConstant = compiler.constantPool.assign(node.form.id.toByteArray())
    emit(Opcode.Native, listOf(formConstant, node.definitions.size.toUInt()))
  }

  private fun emit(code: Opcode) {
    emit(code, emptyList())
  }

  private fun emit(code: Opcode, arguments: List<UInt>) {
    ops.add(StaticOp(Op(code, arguments)))
  }

  private fun patch(code: Opcode, arguments: List<UInt>, index: Int, symbol: CompilableSymbol, rel: MutableRel) {
    ops.add(PatchRelOp(Op(code, arguments), index, symbol, rel))
  }

  private fun patch(code: Opcode, arguments: List<UInt>, index: Int, symbol: CompilableSymbol, rel: UInt) {
    ops.add(PatchRelOp(Op(code, arguments), index, symbol, MutableRel(rel)))
  }

  private fun patch(code: Opcode, arguments: List<UInt>, patches: Map<Int, CompilableSymbol>) {
    ops.add(PatchSymOp(Op(code, arguments), patches))
  }

  fun ops(): List<StubOp> = ops

  private fun load(callOrStubVar: CallOrStubVar) {
    if (callOrStubVar.stubVar != null) {
      emit(Opcode.LoadLocal, listOf(callOrStubVar.stubVar.index))
    } else {
      emit(Opcode.Integer, listOf(ops.size.toUInt() + 2u))
      patch(Opcode.Call, listOf(0u), mapOf(0 to callOrStubVar.call!!))
    }
  }

  private class CallOrStubVar(
    val call: CompilableSymbol? = null,
    val stubVar: StubVar? = null
  )
}
