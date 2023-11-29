package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object AndOpHandler : OpHandler(Opcode.And) {
  override fun handle(machine: InternalMachine, op: Op) {
    val right = machine.pop<Boolean>()
    val left = machine.pop<Boolean>()
    machine.push(left && right)
  }
}
