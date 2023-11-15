package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object NativeOpHandler : OpHandler(Opcode.Native) {
  override fun handle(machine: InternalMachine, op: Op) {
    val countOfNativeDefs = op.args[1].toInt()
    val defs = mutableListOf<Any>()
    for (i in 0 until countOfNativeDefs) {
      defs.add(String(machine.pop() as ByteArray))
    }
  }
}
