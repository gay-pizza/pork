package gay.pizza.pork.vm

import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.bytecode.ConstantTag
import gay.pizza.pork.execution.NativeRegistry

class InternalMachine(val world: CompiledWorld, val nativeRegistry: NativeRegistry, val handlers: List<OpHandler>) {
  private val inlined = world.code.map { op ->
    val handler = handlers.firstOrNull { it.code == op.code } ?:
      throw VirtualMachineException("Opcode ${op.code.name} does not have a handler.")
    op to handler
  }

  private var inst: UInt = 0u
  private val stack = mutableListOf<Any>()
  private val locals = mutableListOf<MutableMap<UInt, Any>>(
    mutableMapOf()
  )
  private val callStack = mutableListOf(0u)
  private val returnAddressStack = mutableListOf<UInt>()
  private var autoNextInst = true
  private var exitFlag = false

  fun step(): Boolean {
    val (op, handler) = inlined[inst.toInt()]
    handler.handle(this, op)
    if (autoNextInst) {
      inst++
    }
    autoNextInst = true
    return !exitFlag
  }

  fun pushScope() {
    locals.add(mutableMapOf())
  }

  fun popScope() {
    locals.removeLast()
  }

  fun loadConstant(id: UInt) {
    val constant = world.constantPool.constants[id.toInt()]
    when (constant.tag) {
      ConstantTag.String -> push(String(constant.value))
      else -> throw VirtualMachineException("Unknown Constant Tag: ${constant.tag.name}")
    }
  }

  fun loadLocal(id: UInt) {
    val localSet = locals.last()
    val local = localSet[id]
      ?: throw VirtualMachineException("Attempted to load local $id but it was not stored.")
    push(local)
  }

  fun storeLocal(id: UInt) {
    val localSet = locals.last()
    val value = popAnyValue()
    localSet[id] = value
  }

  fun setNextInst(value: UInt) {
    inst = value
    autoNextInst = false
  }

  fun pushReturnAddress(value: UInt) {
    returnAddressStack.add(value)
  }

  fun pushCallStack(value: UInt) {
    callStack.add(value)
  }

  fun popCallStack() {
    callStack.removeLast()
  }

  fun armReturnAddressIfSet() {
    val returnAddress = returnAddressStack.removeLastOrNull()
    if (returnAddress != null) {
      setNextInst(returnAddress)
    } else {
      exit()
    }
  }

  fun push(item: Any) {
    stack.add(item)
  }

  fun popAnyValue(): Any = stack.removeLast()

  inline fun <reified T> pop(): T = popAnyValue() as T

  fun exit() {
    exitFlag = true
  }

  fun reset() {
    stack.clear()
    callStack.clear()
    callStack.add(0u)
    locals.clear()
    locals.add(mutableMapOf())
    inst = 0u
    exitFlag = false
    autoNextInst = true
  }
}
