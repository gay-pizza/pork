package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object IndexOpHandler : OpHandler(Opcode.Index) {
  override fun handle(machine: InternalMachine, op: Op) {
    val list = machine.pop<List<*>>()
    val index = machine.pop<Number>().toInt()
    machine.push(list[index] as Any)
  }
}
