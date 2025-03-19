package gay.pizza.pork.compiler

import gay.pizza.pork.bir.*
import gay.pizza.pork.bytecode.ConstantTag
import gay.pizza.pork.bytecode.MutableRel
import gay.pizza.pork.bytecode.Opcode

class IrStubOpEmitter(val irDefinition: IrDefinition, val code: CodeBuilder) : IrCodeVisitor<Unit> {
  private val symbol = code.symbol
  private val functionArgumentCount = irDefinition.arguments.size

  init {
    for (argument in irDefinition.arguments.reversed()) {
      val stubVar = code.localState.createOrFindLocal(argument.symbol)
      code.emit(Opcode.StoreLocal, listOf(stubVar.index))
    }
  }

  fun final() {
    code.emit(Opcode.Return)
    code.emit(Opcode.End)
  }

  private fun resolve(symbol: IrSymbol): Loadable = code.localState.resolve(symbol)

  private fun load(callOrStubVar: Loadable) {
    if (callOrStubVar.stubVar != null) {
      code.emit(Opcode.LoadLocal, listOf(callOrStubVar.stubVar.index))
    } else {
      val retRel = MutableRel(0u)
      retRel.rel = code.nextOpInst() + 2u
      code.patch(Opcode.ReturnAddress, listOf(0u), 0, symbol, retRel)
      code.patch(Opcode.Call, listOf(0u), mapOf(0 to callOrStubVar.call!!))
    }
  }

  private fun store(stubVar: StubVar) {
    code.emit(Opcode.StoreLocal, listOf(stubVar.index))
  }

  override fun visitIrBeak(ir: IrBreak) {
    val loop = code.localState.findLoopState(ir.target)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, loop.exitJumpTarget)
  }

  override fun visitIrCall(ir: IrCall) {
    val target = resolve(ir.target)
    val targetSymbol = target.call!!
    val retRel = MutableRel(0u)
    for (argument in ir.arguments) {
      visit(argument)
    }
    val variableArguments = ir.variableArguments
    if (variableArguments != null) {
      for (argument in variableArguments) {
        visit(argument)
      }
      code.emit(Opcode.ListMake, listOf(variableArguments.size.toUInt()))
    }
    retRel.rel = code.nextOpInst() + 2u
    code.patch(Opcode.ReturnAddress, listOf(0u), 0, symbol, retRel)
    code.patch(Opcode.Call, listOf(0u), mapOf(0 to targetSymbol))
  }

  override fun visitIrCodeBlock(ir: IrCodeBlock) {
    for (item in ir.items) {
      visit(item)
    }
  }

  override fun visitIrConditional(ir: IrConditional) {
    val thenRel = MutableRel(0u)
    val endRel = MutableRel(0u)
    visit(ir.conditional)
    code.patch(Opcode.JumpIf, listOf(0u), 0, symbol, thenRel)
    visit(ir.ifFalse)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, endRel)
    thenRel.rel = code.nextOpInst()
    visit(ir.ifTrue)
    endRel.rel = code.nextOpInst()
  }

  override fun visitIrBooleanConstant(ir: IrBooleanConstant) {
    code.emit(if (ir.value) Opcode.True else Opcode.False)
  }

  override fun visitIrIntegerConstant(ir: IrIntegerConstant) {
    code.emit(Opcode.Integer, listOf(ir.value.toUInt()))
  }

  override fun visitIrLongConstant(ir: IrLongConstant) {
    val value1 = ir.value.toUInt()
    val value2 = (ir.value shr 32).toUInt()
    code.emit(Opcode.Long, listOf(value1, value2))
  }

  override fun visitIrDoubleConstant(ir: IrDoubleConstant) {
    val value = ir.value.toRawBits()
    val value1 = value.toUInt()
    val value2 = (value shr 32).toUInt()
    code.emit(Opcode.Double, listOf(value1, value2))
  }

  override fun visitIrStringConstant(ir: IrStringConstant) {
    val bytes = ir.value.toByteArray()
    val constant = symbol.compilableSlab.compiler.constantPool.assign(ConstantTag.String, bytes)
    code.emit(Opcode.Constant, listOf(constant))
  }

  override fun visitIrNoneConstant(ir: IrNoneConstant) {
    code.emit(Opcode.None)
  }

  override fun visitIrContinue(ir: IrContinue) {
    val loop = code.localState.findLoopState(ir.target)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, loop.exitJumpTarget)
  }

  override fun visitIrInfix(ir: IrInfix) {
    visit(ir.left)
    visit(ir.right)
    when (ir.op) {
      IrInfixOp.Add -> code.emit(Opcode.Add)
      IrInfixOp.Subtract -> code.emit(Opcode.Subtract)
      IrInfixOp.Multiply -> code.emit(Opcode.Multiply)
      IrInfixOp.Divide -> code.emit(Opcode.Divide)
      IrInfixOp.Equals -> code.emit(Opcode.CompareEqual)
      IrInfixOp.NotEquals -> {
        code.emit(Opcode.CompareEqual)
        code.emit(Opcode.Not)
      }

      IrInfixOp.EuclideanModulo -> code.emit(Opcode.EuclideanModulo)
      IrInfixOp.Remainder -> code.emit(Opcode.Remainder)
      IrInfixOp.Lesser -> code.emit(Opcode.CompareLesser)
      IrInfixOp.Greater -> code.emit(Opcode.CompareGreater)
      IrInfixOp.GreaterEqual -> code.emit(Opcode.CompareGreaterEqual)
      IrInfixOp.LesserEqual -> code.emit(Opcode.CompareLesserEqual)
      IrInfixOp.BooleanAnd -> code.emit(Opcode.And)
      IrInfixOp.BooleanOr -> code.emit(Opcode.Or)
      IrInfixOp.BinaryAnd -> code.emit(Opcode.BinaryAnd)
      IrInfixOp.BinaryOr -> code.emit(Opcode.BinaryOr)
      IrInfixOp.BinaryExclusiveOr -> code.emit(Opcode.BinaryXor)
    }
  }

  override fun visitIrList(ir: IrList) {
    val count = ir.items.size
    for (item in ir.items) {
      visit(item)
    }
    code.emit(Opcode.ListMake, listOf(count.toUInt()))
  }

  override fun visitIrLoad(ir: IrLoad) {
    val loadable = resolve(ir.target)
    load(loadable)
  }

  override fun visitIrLoop(ir: IrLoop) {
    val startOfBody = MutableRel(0u)
    val startOfLoop = MutableRel(0u)
    val endOfLoop = MutableRel(0u)
    code.localState.startLoop(ir.symbol, code.nextOpInst(), endOfLoop)
    startOfLoop.rel = code.nextOpInst()
    visit(ir.condition)
    code.patch(Opcode.JumpIf, listOf(0u), 0, symbol, startOfBody)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, endOfLoop)
    startOfBody.rel = code.nextOpInst()
    visit(ir.inner)
    code.patch(Opcode.Jump, listOf(0u), 0, symbol, startOfLoop)
    endOfLoop.rel = code.nextOpInst()
    code.localState.endLoop(ir.symbol)
  }

  override fun visitIrPrefix(ir: IrPrefix) {
    visit(ir.value)
    when (ir.op) {
      IrPrefixOp.BooleanNot -> code.emit(Opcode.Not)
      IrPrefixOp.UnaryPlus -> code.emit(Opcode.UnaryPlus)
      IrPrefixOp.UnaryMinus -> code.emit(Opcode.UnaryMinus)
      IrPrefixOp.BinaryNot -> code.emit(Opcode.BinaryNot)
    }
  }

  override fun visitIrReturn(ir: IrReturn) {
    visit(ir.value)
    code.emit(Opcode.Return)
  }

  override fun visitIrDeclare(ir: IrDeclare) {
    visit(ir.value)
    val variable = code.localState.createOrFindLocal(ir.symbol)
    store(variable)
  }

  override fun visitIrStore(ir: IrStore) {
    visit(ir.value)
    val variable = code.localState.createOrFindLocal(ir.target)
    store(variable)
  }

  override fun visitIrSuffix(ir: IrSuffix) {
    val loadable = code.localState.resolve(ir.target)
    load(loadable)
    when (ir.op) {
      IrSuffixOp.Increment -> {
        code.emit(Opcode.Integer, listOf(1u))
        code.emit(Opcode.Add, emptyList())
      }

      IrSuffixOp.Decrement -> {
        code.emit(Opcode.Integer, listOf(1u))
        code.emit(Opcode.Subtract, emptyList())
      }
    }
    store(loadable.stubVar!!)
  }

  override fun visitIrNativeDefinition(ir: IrNativeDefinition) {
    val encodedDefinitions = ir.definitions.map { def -> def.encodeToByteArray() }.toMutableList()
    encodedDefinitions.add(0, ir.form.encodeToByteArray())
    val buffer = ByteArray(encodedDefinitions.sumOf { it.size } + encodedDefinitions.size - 1) { 0 }
    var i = 0
    for ((index, encoded) in encodedDefinitions.withIndex()) {
      encoded.copyInto(buffer, i, 0)
      i += encoded.size
      if (index != encodedDefinitions.lastIndex) {
        i += 1
      }
    }
    val nativeDefinitionConstant = symbol.compilableSlab.compiler.constantPool.assign(
      ConstantTag.NativeDefinition,
      buffer,
    )
    code.emit(Opcode.Native, listOf(nativeDefinitionConstant, functionArgumentCount.toUInt()))
  }

  override fun visitIrIndex(ir: IrIndex) {
    visit(ir.index)
    visit(ir.data)
    code.emit(Opcode.Index)
  }

  override fun visitIrListSize(ir: IrListSize) {
    visit(ir.list)
    code.emit(Opcode.ListSize)
  }
}
