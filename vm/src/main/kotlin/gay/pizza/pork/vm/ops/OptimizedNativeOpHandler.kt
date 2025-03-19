package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.execution.NativeFunction
import gay.pizza.pork.execution.None
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

class OptimizedNativeOpHandler(val function: NativeFunction) : OpHandler(Opcode.Native) {
  override fun handle(machine: InternalMachine, op: Op) {
    val argumentCount = op.args[1]
    val arguments = mutableListOf<Any>()
    var x = argumentCount
    while (x > 0u) {
      x--
      arguments.add(machine.localAt(x))
    }
    val result = function.invoke(arguments)
    machine.push(if (result == Unit) None else result)
  }
}
