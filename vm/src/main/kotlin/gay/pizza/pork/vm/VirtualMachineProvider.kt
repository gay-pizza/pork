package gay.pizza.pork.vm

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.compiler.Compiler
import gay.pizza.pork.execution.ExecutionContext
import gay.pizza.pork.execution.ExecutionContextProvider
import gay.pizza.pork.execution.NativeRegistry
import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.World

class VirtualMachineProvider(val world: World) : ExecutionContextProvider {
  override fun prepare(importLocator: ImportLocator, entryPointSymbol: Symbol, nativeRegistry: NativeRegistry): ExecutionContext {
    val compiler = Compiler()
    val slab = world.load(importLocator)
    val compilableSlab = compiler.compilableSlabs.of(slab)
    val compilableSymbol = compilableSlab.resolve(entryPointSymbol) ?:
      throw RuntimeException("Unable to find compilable symbol for entry point '${entryPointSymbol.id}'")
    val compiledWorld = compiler.compile(compilableSymbol)
    return VirtualMachine(compiledWorld, nativeRegistry)
  }
}
