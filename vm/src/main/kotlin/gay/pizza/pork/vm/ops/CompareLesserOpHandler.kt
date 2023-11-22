package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object CompareLesserOpHandler : OpHandler(Opcode.CompareLesser) {
  override fun handle(machine: InternalMachine, op: Op) {
    val right = machine.pop<Int>()
    val left = machine.pop<Int>()
    machine.push(left < right)
  }
}
