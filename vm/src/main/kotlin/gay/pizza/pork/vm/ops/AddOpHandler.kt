package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler
import gay.pizza.pork.vm.VirtualMachineException

object AddOpHandler : OpHandler(Opcode.Add) {
  override fun handle(machine: InternalMachine, op: Op) {
    val left = machine.pop()
    val right = machine.pop()

    if (left !is Int || right !is Int) {
      throw VirtualMachineException("Bad types.")
    }
    machine.push(left + right)
  }
}
