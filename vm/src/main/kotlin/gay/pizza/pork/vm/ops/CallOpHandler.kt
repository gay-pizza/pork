package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object CallOpHandler : OpHandler(Opcode.Call) {
  override fun handle(machine: InternalMachine, op: Op) {
    machine.setNextInst(op.args[0])
    machine.pushCallStack(op.args[0])
    machine.pushScope()
  }
}
