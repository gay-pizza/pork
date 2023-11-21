package gay.pizza.pork.vm

import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.execution.ExecutionContext
import gay.pizza.pork.vm.ops.*

class VirtualMachine(world: CompiledWorld) : ExecutionContext {
  private val internal = InternalMachine(world, listOf(
    IntegerOpHandler,
    ConstantOpHandler,

    TrueOpHandler,
    FalseOpHandler,

    ListMakeOpHandler,
    ListSizeOpHandler,

    IndexOpHandler,

    AndOpHandler,
    OrOpHandler,
    NotOpHandler,

    CompareEqualOpHandler,
    CompareLesserEqualOpHandler,
    CompareGreaterEqualOpHandler,

    AddOpHandler,

    JumpOpHandler,
    JumpIfOpHandler,

    LoadLocalOpHandler,
    StoreLocalOpHandler,

    ReturnAddressOpHandler,
    CallOpHandler,
    ReturnOpHandler,

    NativeOpHandler,

    ScopeInOpHandler,
    ScopeOutOpHandler,

    EndOpHandler
  ))

  override fun execute() {
    while (true) {
      if (!internal.step()) {
        break
      }
    }
    internal.reset()
  }
}
