package gay.pizza.pork.compiler

import gay.pizza.pork.ast.FunctionLevelVisitor
import gay.pizza.pork.ast.gen.*
import gay.pizza.pork.bytecode.ConstantTag
import gay.pizza.pork.bytecode.MutableRel
import gay.pizza.pork.bytecode.Opcode

class StubOpEmitter(val compiler: Compiler, val symbol: CompilableSymbol) : FunctionLevelVisitor<Unit>() {
  val code: CodeBuilder = CodeBuilder(symbol)

  fun allocateOuterScope(definition: FunctionDefinition) {
    val allNormalArguments = definition.arguments.takeWhile { !it.multiple }
    val varArgument = definition.arguments.firstOrNull { it.multiple }

    for (arg in allNormalArguments.reversed()) {
      val functionLocal = code.localState.createLocal(arg.symbol)
      code.emit(Opcode.StoreLocal, listOf(functionLocal.index))
    }

    if (varArgument != null) {
      val functionLocal = code.localState.createLocal(varArgument.symbol)
      code.emit(Opcode.StoreLocal, listOf(functionLocal.index))
    }
  }

  fun enter() {
    code.localState.pushScope()
  }

  fun exit() {
    code.localState.popScope()
    code.emit(Opcode.None)
    code.emit(Opcode.Return)
  }

  override fun visitBlock(node: Block) {
    code.localState.pushScope()
    node.visitChildren(this)
    code.localState.popScope()
  }

  override fun visitBooleanLiteral(node: BooleanLiteral) {
    code.emit(if (node.value) Opcode.True else Opcode.False)
  }

  override fun visitBreak(node: Break) {
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, code.localState.loopState!!.exitJumpTarget)
  }

  override fun visitContinue(node: Continue) {
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, code.localState.loopState!!.startOfLoop)
  }

  override fun visitDoubleLiteral(node: DoubleLiteral) {
    code.emit(Opcode.Integer, listOf(node.value.toUInt()))
  }

  override fun visitForIn(node: ForIn) {
    val listLocalVar = code.localState.createAnonymousLocal()
    val sizeLocalVar = code.localState.createAnonymousLocal()
    val currentIndexVar = code.localState.createAnonymousLocal()
    val currentValueVar = code.localState.createLocal(node.item.symbol)
    node.expression.visit(this)
    code.emit(Opcode.StoreLocal, listOf(listLocalVar.index))
    load(Loadable(stubVar = listLocalVar))
    code.emit(Opcode.ListSize)
    code.emit(Opcode.StoreLocal, listOf(sizeLocalVar.index))
    code.emit(Opcode.Integer, listOf(0u))
    code.emit(Opcode.StoreLocal, listOf(currentIndexVar.index))
    val endOfLoop = MutableRel(0u)
    val startOfLoop = code.nextOpInst()
    code.localState.startLoop(startOfLoop, endOfLoop)
    load(Loadable(stubVar = currentIndexVar))
    load(Loadable(stubVar = sizeLocalVar))
    code.emit(Opcode.CompareGreaterEqual)
    code.patch(Opcode.JumpIf, listOf(0u), 0, symbol, endOfLoop)
    load(Loadable(stubVar = currentIndexVar))
    load(Loadable(stubVar = listLocalVar))
    code.emit(Opcode.Index)
    code.emit(Opcode.StoreLocal, listOf(currentValueVar.index))
    node.block.visit(this)
    code.emit(Opcode.LoadLocal, listOf(currentIndexVar.index))
    code.emit(Opcode.Integer, listOf(1u))
    code.emit(Opcode.Add)
    code.emit(Opcode.StoreLocal, listOf(currentIndexVar.index))
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, startOfLoop)
    endOfLoop.rel = code.nextOpInst()
  }

  override fun visitFunctionCall(node: FunctionCall) {
    val targetScopeSymbol = symbol.scopeSymbol.scope.resolve(node.symbol) ?:
      throw RuntimeException("Unable to resolve symbol: ${node.symbol.id}")
    val targetSymbol = compiler.resolve(targetScopeSymbol)
    val functionDefinition = targetSymbol.scopeSymbol.definition as FunctionDefinition
    val retRel = MutableRel(0u)
    val normalArguments = mutableListOf<Expression>()
    var variableArguments: List<Expression>? = null
    if (functionDefinition.arguments.any { it.multiple }) {
      variableArguments = emptyList()
    }

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
      code.emit(Opcode.ListMake, listOf(variableArguments.size.toUInt()))
    }

    for (item in normalArguments.reversed()) {
      visit(item)
    }

    retRel.rel = code.nextOpInst() + 2u
    code.patch(Opcode.ReturnAddress, listOf(0u), 0, symbol, retRel)
    code.patch(Opcode.Call, listOf(0u), mapOf(0 to targetSymbol))
  }

  override fun visitIf(node: If) {
    val thenRel = MutableRel(0u)
    val endRel = MutableRel(0u)
    node.condition.visit(this)
    code.patch(Opcode.JumpIf, listOf(0u), 0, symbol, thenRel)
    node.elseBlock?.visit(this)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, endRel)
    thenRel.rel = code.nextOpInst()
    node.thenBlock.visit(this)
    endRel.rel = code.nextOpInst()
  }

  override fun visitIndexedBy(node: IndexedBy) {
    node.expression.visit(this)
    node.index.visit(this)
    code.emit(Opcode.Index)
  }

  override fun visitInfixOperation(node: InfixOperation) {
    node.left.visit(this)
    node.right.visit(this)
    when (node.op) {
      InfixOperator.Plus -> code.emit(Opcode.Add)
      InfixOperator.Minus -> code.emit(Opcode.Subtract)
      InfixOperator.Multiply -> code.emit(Opcode.Multiply)
      InfixOperator.Divide -> code.emit(Opcode.Divide)
      InfixOperator.Equals -> code.emit(Opcode.CompareEqual)
      InfixOperator.NotEquals -> {
        code.emit(Opcode.CompareEqual)
        code.emit(Opcode.Not)
      }
      InfixOperator.EuclideanModulo -> code.emit(Opcode.EuclideanModulo)
      InfixOperator.Remainder -> code.emit(Opcode.Remainder)
      InfixOperator.Lesser -> code.emit(Opcode.CompareLesser)
      InfixOperator.Greater -> code.emit(Opcode.CompareGreater)
      InfixOperator.GreaterEqual -> code.emit(Opcode.CompareGreaterEqual)
      InfixOperator.LesserEqual -> code.emit(Opcode.CompareLesserEqual)
      InfixOperator.BooleanAnd -> code.emit(Opcode.And)
      InfixOperator.BooleanOr -> code.emit(Opcode.Or)
      InfixOperator.BinaryAnd -> code.emit(Opcode.BinaryAnd)
      InfixOperator.BinaryOr -> code.emit(Opcode.BinaryOr)
      InfixOperator.BinaryExclusiveOr -> code.emit(Opcode.BinaryXor)
    }
  }

  override fun visitIntegerLiteral(node: IntegerLiteral) {
    code.emit(Opcode.Integer, listOf(node.value.toUInt()))
  }

  override fun visitLetAssignment(node: LetAssignment) {
    val variable = code.localState.createLocal(node.symbol)
    node.value.visit(this)
    code.emit(Opcode.StoreLocal, listOf(variable.index))
  }

  override fun visitListLiteral(node: ListLiteral) {
    val count = node.items.size
    for (item in node.items) {
      item.visit(this)
    }
    code.emit(Opcode.ListMake, listOf(count.toUInt()))
  }

  override fun visitLongLiteral(node: LongLiteral) {
    code.emit(Opcode.Integer, listOf(node.value.toUInt()))
  }

  override fun visitNoneLiteral(node: NoneLiteral) {
    code.emit(Opcode.None)
  }

  override fun visitParentheses(node: Parentheses) {
    node.expression.visit(this)
  }

  override fun visitPrefixOperation(node: PrefixOperation) {
    node.expression.visit(this)
    when (node.op) {
      PrefixOperator.BooleanNot -> code.emit(Opcode.Not)
      PrefixOperator.UnaryPlus -> code.emit(Opcode.UnaryPlus)
      PrefixOperator.UnaryMinus -> code.emit(Opcode.UnaryMinus)
      PrefixOperator.BinaryNot -> code.emit(Opcode.BinaryNot)
    }
  }

  override fun visitReturn(node: Return) {
    node.value.visit(this)
    code.emit(Opcode.Return)
  }

  override fun visitSetAssignment(node: SetAssignment) {
    val stubVarOrCall = code.localState.resolve(node.symbol)
    if (stubVarOrCall.stubVar == null) {
      throw RuntimeException("Invalid set assignment.")
    }
    node.value.visit(this)
    code.emit(Opcode.StoreLocal, listOf(stubVarOrCall.stubVar.index))
  }

  override fun visitStringLiteral(node: StringLiteral) {
    val bytes = node.text.toByteArray()
    val constant = compiler.constantPool.assign(ConstantTag.String, bytes)
    code.emit(Opcode.Constant, listOf(constant))
  }

  override fun visitSuffixOperation(node: SuffixOperation) {
    val stubVarOrCall = code.localState.resolve(node.reference.symbol)
    if (stubVarOrCall.stubVar == null) {
      throw RuntimeException("Invalid suffix operation.")
    }
    load(stubVarOrCall)
    when (node.op) {
      SuffixOperator.Increment -> {
        code.emit(Opcode.Integer, listOf(1u))
        code.emit(Opcode.Add, emptyList())
        code.emit(Opcode.StoreLocal, listOf(stubVarOrCall.stubVar.index))
      }
      SuffixOperator.Decrement -> {
        code.emit(Opcode.Integer, listOf(1u))
        code.emit(Opcode.Subtract, emptyList())
        code.emit(Opcode.StoreLocal, listOf(stubVarOrCall.stubVar.index))
      }
    }
  }

  override fun visitSymbolReference(node: SymbolReference) {
    val variable = code.localState.resolve(node.symbol)
    load(variable)
  }

  override fun visitVarAssignment(node: VarAssignment) {
    val variable = code.localState.createLocal(node.symbol)
    node.value.visit(this)
    code.emit(Opcode.StoreLocal, listOf(variable.index))
  }

  override fun visitWhile(node: While) {
    val startOfBody = MutableRel(0u)
    val startOfLoop = MutableRel(0u)
    val endOfLoop = MutableRel(0u)
    code.localState.startLoop(code.nextOpInst(), endOfLoop)
    startOfLoop.rel = code.nextOpInst()
    node.condition.visit(this)
    code.patch(Opcode.JumpIf, listOf(0u), 0, symbol, startOfBody)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, endOfLoop)
    startOfBody.rel = code.nextOpInst()
    node.block.visit(this)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, startOfLoop)
    endOfLoop.rel = code.nextOpInst()
    code.localState.endLoop()
  }

  override fun visitNativeFunctionDescriptor(node: NativeFunctionDescriptor) {
    for (def in node.definitions) {
      val defConstant = compiler.constantPool.assign(ConstantTag.String, def.text.toByteArray())
      code.emit(Opcode.Constant, listOf(defConstant))
    }
    val formConstant = compiler.constantPool.assign(ConstantTag.String, node.form.id.toByteArray())
    code.emit(Opcode.Native, listOf(formConstant, node.definitions.size.toUInt()))
  }

  private fun load(callOrStubVar: Loadable) {
    if (callOrStubVar.stubVar != null) {
      code.emit(Opcode.LoadLocal, listOf(callOrStubVar.stubVar.index))
    } else {
      code.emit(Opcode.Integer, listOf(code.nextOpInst() + 2u))
      code.patch(Opcode.Call, listOf(0u), mapOf(0 to callOrStubVar.call!!))
    }
  }
}
