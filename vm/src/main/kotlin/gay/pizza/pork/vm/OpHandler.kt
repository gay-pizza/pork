package gay.pizza.pork.vm

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode

abstract class OpHandler(val code: Opcode) {
  abstract fun handle(machine: InternalMachine, op: Op)

  open fun optimize(machine: InternalMachine, op: Op): OpHandler? = null
}
