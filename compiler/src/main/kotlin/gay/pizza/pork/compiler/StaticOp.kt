package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.Op

class StaticOp(op: Op) : StubOp(op) {
  override fun toString(): String = "StaticOp(${op})"
}
