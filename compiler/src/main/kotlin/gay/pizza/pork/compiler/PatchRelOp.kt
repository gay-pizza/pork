package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.MutableRel
import gay.pizza.pork.bytecode.Op

class PatchRelOp(op: Op, val index: Int, val symbol: CompilableSymbol, val rel: MutableRel) : StubOp(op) {
  override fun patch(context: StubResolutionContext, arguments: MutableList<UInt>) {
    arguments[index] = context.resolveJumpTarget(symbol) + rel.rel
  }

  override fun toString(): String = "PatchRelOp(${op}, ${index}, ${symbol}, ${rel})"
}
