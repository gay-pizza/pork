package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.MutableRel

class LoopState(
  val startOfLoop: UInt,
  val exitJumpTarget: MutableRel,
  val body: MutableRel,
  val scopeDepth: Int,
  val enclosing: LoopState? = null
)
