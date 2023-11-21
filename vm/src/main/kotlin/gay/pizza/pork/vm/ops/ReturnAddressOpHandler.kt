package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object ReturnAddressOpHandler : OpHandler(Opcode.ReturnAddress) {
  override fun handle(machine: InternalMachine, op: Op) {
    machine.pushReturnAddress(op.args[0])
  }
}
