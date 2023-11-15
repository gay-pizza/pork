package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.Op

abstract class StubOp(val op: Op) {
  open fun patch(context: StubResolutionContext, arguments: MutableList<UInt>) {}
}
