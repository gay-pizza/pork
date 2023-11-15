package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler
import gay.pizza.pork.vm.VirtualMachineException

object JumpIfOpHandler : OpHandler(Opcode.JumpIf) {
  override fun handle(machine: InternalMachine, op: Op) {
    val value = machine.pop()
    if (value !is Boolean) {
      throw VirtualMachineException("JumpIf expects a boolean value on the stack.")
    }

    if (value) {
      machine.setNextInst(op.args[0])
    }
  }
}
