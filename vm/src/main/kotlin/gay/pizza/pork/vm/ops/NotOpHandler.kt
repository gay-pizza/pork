package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object NotOpHandler : OpHandler(Opcode.Not) {
  override fun handle(machine: InternalMachine, op: Op) {
    machine.push(!machine.pop<Boolean>())
  }
}
