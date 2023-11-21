package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object ListMakeOpHandler : OpHandler(Opcode.ListMake) {
  override fun handle(machine: InternalMachine, op: Op) {
    val count = op.args[0]
    val list = mutableListOf<Any>()
    for (i in 1u..count) {
      val item = machine.popAnyValue()
      list.add(item)
    }
    machine.push(list.reversed())
  }
}
