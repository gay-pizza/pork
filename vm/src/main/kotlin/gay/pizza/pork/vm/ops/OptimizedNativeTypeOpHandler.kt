package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.execution.NativeFunction
import gay.pizza.pork.execution.NativeType
import gay.pizza.pork.execution.None
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

class OptimizedNativeTypeOpHandler(val type: NativeType) : OpHandler(Opcode.NativeType) {
  override fun handle(machine: InternalMachine, op: Op) {
    val result = type.value()
    machine.push(if (result == Unit) None else result)
  }
}
