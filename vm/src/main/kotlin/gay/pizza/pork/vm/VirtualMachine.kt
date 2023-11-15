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

    ListOpHandler,

    CompareEqualOpHandler,
    CompareLesserEqualOpHandler,

    AddOpHandler,

    JumpOpHandler,
    JumpIfOpHandler,

    LoadLocalOpHandler,
    StoreLocalOpHandler,

    CallOpHandler,
    RetOpHandler,

    NativeOpHandler,

    ScopeInOpHandler,
    ScopeOutOpHandler
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
