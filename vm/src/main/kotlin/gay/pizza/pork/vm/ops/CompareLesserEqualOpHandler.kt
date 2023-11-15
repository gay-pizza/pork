package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler
import gay.pizza.pork.vm.VirtualMachineException

object CompareLesserEqualOpHandler : OpHandler(Opcode.CompareLesserEqual) {
  override fun handle(machine: InternalMachine, op: Op) {
    val right = machine.pop()
    val left = machine.pop()

    if (left !is Int || right !is Int) {
      throw VirtualMachineException("Bad types.")
    }
    machine.push(left <= right)
  }
}
