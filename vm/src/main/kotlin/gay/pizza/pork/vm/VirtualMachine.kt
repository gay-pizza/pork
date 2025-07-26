package gay.pizza.pork.vm

import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.execution.ExecutionContext
import gay.pizza.pork.execution.NativeRegistry

class VirtualMachine(world: CompiledWorld, nativeRegistry: NativeRegistry, debug: Boolean = false) : ExecutionContext {
  private val internal = InternalMachine(
    world = world,
    nativeRegistry = nativeRegistry,
    handlers = StandardOpHandlers,
    debug = debug,
  )

  override fun execute() {
    internal.reset()
    while (true) {
      if (!internal.step()) {
        break
      }
    }
  }
}
