package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.MutableRel

class LoopState(
  val startOfLoop: UInt,
  val exitJumpTarget: MutableRel,
  val scopeDepth: UInt,
  val enclosing: LoopState? = null
)
