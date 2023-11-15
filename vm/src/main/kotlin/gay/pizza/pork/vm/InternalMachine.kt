package gay.pizza.pork.vm

import gay.pizza.pork.bytecode.CompiledWorld

class InternalMachine(val world: CompiledWorld, val handlers: List<OpHandler>) {
  private val inlined = world.code.map { op ->
    val handler = handlers.firstOrNull { it.code == op.code } ?:
      throw VirtualMachineException("Opcode ${op.code.name} does not have a handler.")
    op to handler
  }

  private var inst: UInt = 0u
  private val stack = mutableListOf<Any>(EndOfCode)
  private val locals = mutableListOf<MutableMap<UInt, Any>>(
    mutableMapOf()
  )
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
    push(world.constantPool.constants[id.toInt()])
  }

  fun loadLocal(id: UInt) {
    val localSet = locals.last()
    val local = localSet[id]
      ?: throw VirtualMachineException("Attempted to load local $id but it was not stored.")
    push(local)
  }

  fun storeLocal(id: UInt) {
    val localSet = locals.last()
    val value = pop()
    localSet[id] = value
  }

  fun setNextInst(value: UInt) {
    inst = value
    autoNextInst = false
  }

  fun push(item: Any) {
    stack.add(item)
  }

  fun pop(): Any = stack.removeLast()
  fun exit() {
    exitFlag = true
  }

  fun reset() {
    stack.clear()
    stack.add(EndOfCode)
    locals.clear()
    locals.add(mutableMapOf())
    inst = 0u
    exitFlag = false
    autoNextInst = true
  }

  data object EndOfCode
}
