package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.MutableRel
import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode

class CodeBuilder(val symbol: CompilableSymbol) {
  private val ops = mutableListOf<StubOp>()
  private val annotations = mutableListOf<StubOpAnnotation>()

  val localState: LocalState = LocalState(symbol)

  fun nextOpInst(): UInt = ops.size.toUInt()

  fun emit(op: StubOp) {
    ops.add(op)
  }

  fun emit(op: Op) {
    ops.add(StaticOp(op))
  }

  fun emit(code: Opcode, arguments: List<UInt>) {
    emit(Op(code, arguments))
  }

  fun emit(code: Opcode) {
    emit(code, emptyList())
  }

  fun patch(code: Opcode, arguments: List<UInt>, index: Int, symbol: CompilableSymbol, rel: MutableRel) {
    emit(PatchRelOp(Op(code, arguments), index, symbol, rel))
  }

  fun patch(code: Opcode, arguments: List<UInt>, index: Int, symbol: CompilableSymbol, rel: UInt) {
    emit(PatchRelOp(Op(code, arguments), index, symbol, MutableRel(rel)))
  }

  fun patch(code: Opcode, arguments: List<UInt>, patches: Map<Int, CompilableSymbol>) {
    ops.add(PatchSymOp(Op(code, arguments), patches))
  }

  fun lastOp(): StubOp? = ops.lastOrNull()

  fun annotate(text: String) {
    annotations.add(StubOpAnnotation(symbol, nextOpInst(), text))
  }

  fun build(): CompiledSymbolResult = CompiledSymbolResult(ops.toList(), annotations.toList())
}
