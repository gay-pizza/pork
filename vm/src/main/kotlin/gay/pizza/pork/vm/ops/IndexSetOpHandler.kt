package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object IndexSetOpHandler : OpHandler(Opcode.IndexSet) {
  override fun handle(machine: InternalMachine, op: Op) {
    val list = machine.pop<MutableList<Any>>()
    val index = machine.pop<Number>().toInt()
    val value = machine.pop<Any>()
    list[index] = value
  }
}
