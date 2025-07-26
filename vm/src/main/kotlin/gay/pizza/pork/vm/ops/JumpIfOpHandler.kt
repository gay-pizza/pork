package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object JumpIfOpHandler : OpHandler(Opcode.JumpIf) {
  override fun handle(machine: InternalMachine, op: Op) {
    val value = machine.pop<Boolean>()
    if (value) {
      machine.setNextInst(op.args[0])
    }
  }
}
