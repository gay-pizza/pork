package gay.pizza.pork.vm.ops

import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.vm.InternalMachine
import gay.pizza.pork.vm.OpHandler

object NativeTypeOpHandler : OpHandler(Opcode.NativeType) {
  override fun handle(machine: InternalMachine, op: Op) {
    val handler = optimize(machine, op)
    handler.handle(machine, op)
  }

  override fun optimize(machine: InternalMachine, op: Op): OpHandler {
    val nativeDefinition = machine.world.constantPool.read(op.args[0]).readAsNativeDefinition()
    val form = nativeDefinition[0]
    val provider = machine.nativeRegistry.of(form)
    val type = provider.provideNativeType(nativeDefinition.subList(1, nativeDefinition.size))
    return OptimizedNativeTypeOpHandler(type)
  }
}
