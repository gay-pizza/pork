package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.Op

class PatchSymOp(op: Op, val patches: Map<Int, CompilableSymbol>) : StubOp(op) {
  override fun patch(context: StubResolutionContext, arguments: MutableList<UInt>) {
    for ((index, symbol) in patches) {
      arguments[index] = context.resolveJumpTarget(symbol)
    }
  }

  override fun toString(): String = "PatchSymOp(${op}, ${patches})"
}
