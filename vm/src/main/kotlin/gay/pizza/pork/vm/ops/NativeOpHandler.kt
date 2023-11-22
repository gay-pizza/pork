package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object NativeOpHandler : OpHandler(Opcode.Native) {
  override fun handle(machine: InternalMachine, op: Op) {
    val argumentCount = op.args[2]
    val arguments = mutableListOf<Any>()
    for (i in 0u until argumentCount) {
      machine.loadLocal(i)
      arguments.add(machine.popAnyValue())
    }
    val formConstant = machine.world.constantPool.read(op.args[0])
    val form = formConstant.readAsString()
    val provider = machine.nativeRegistry.of(form)
    val countOfNativeDefs = op.args[1].toInt()
    val defs = mutableListOf<String>()
    for (i in 0 until countOfNativeDefs) {
      defs.add(machine.pop())
    }
    val function = provider.provideNativeFunction(defs)
    function.invoke(arguments)
  }
}
