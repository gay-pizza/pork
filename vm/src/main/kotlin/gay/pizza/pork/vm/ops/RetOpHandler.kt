package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object RetOpHandler : OpHandler(Opcode.Return) {
  override fun handle(machine: InternalMachine, op: Op) {
    val last = machine.pop()
    if (last == InternalMachine.EndOfCode) {
      machine.exit()
      return
    }
    machine.popScope()
    machine.setNextInst((last as Int).toUInt())
  }
}
