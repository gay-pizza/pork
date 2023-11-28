package gay.pizza.pork.vm

import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.bytecode.Op
import gay.pizza.pork.bytecode.SymbolInfo

data class StackFrame(val symbolInfo: SymbolInfo, val rel: UInt) {
  val worldCodeOffset: UInt
    get() = symbolInfo.offset + rel

  fun opInWorld(world: CompiledWorld): Op =
    world.code[worldCodeOffset.toInt()]

  fun describeInWorld(world: CompiledWorld): String =
    "StackFrame(${worldCodeOffset}, ${symbolInfo.slab} ${symbolInfo.symbol} + ${rel}, ${opInWorld(world)})"

  override fun toString(): String =
    "StackFrame(${worldCodeOffset}, ${symbolInfo.slab} ${symbolInfo.symbol} + ${rel})"
}
