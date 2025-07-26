package gay.pizza.pork.vm

import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.bytecode.ConstantTag
import gay.pizza.pork.execution.NativeRegistry

class InternalMachine(val world: CompiledWorld, val nativeRegistry: NativeRegistry, val handlers: List<OpHandler>, val debug: Boolean = false) {
  private val inlined = world.code.map { op ->
    val handler = handlers.firstOrNull { it.code == op.code }
      ?: throw VirtualMachineException("Opcode ${op.code.name} does not have a handler.")
    op to (handler.optimize(machine = this, op) ?: handler)
  }

  private var inst: UInt = 0u
  private val stack = mutableListOf<Any>()
  private val locals = mutableListOf(
    LocalSlots()
  )
  private val callStack = mutableListOf(0u)
  private val returnAddressStack = mutableListOf<UInt>()
  private var autoNextInst = true
  private var exitFlag = false

  fun step(): Boolean {
    val (op, handler) = inlined[inst.toInt()]
    if (debug) {
      val frame = frame(inst)
      println("vm: step: in slab ${frame?.symbolInfo?.slab ?: "unknown"}: symbol ${frame?.symbolInfo?.symbol ?: "unknown"}: $inst ${op.code}${if (op.args.isEmpty()) "" else " " + op.args.joinToString(" ")}")
      println("vm: step: stack: $stack")
    }

    handler.handle(this, op)
    if (autoNextInst) {
      inst++
    }
    autoNextInst = true
    return !exitFlag
  }

  fun pushScope() {
    if (debug) {
      println("  vm: push scope")
    }
    locals.add(LocalSlots())
  }

  fun popScope() {
    if (debug) {
      println("  vm: pop scope")
    }
    locals.removeLast()
  }

  fun loadConstant(id: UInt) {
    val constant = world.constantPool.constants[id.toInt()]
    val value = when (constant.tag) {
      ConstantTag.String -> String(constant.value)
      else -> throw VirtualMachineException("Unknown constant tag: ${constant.tag.name}")
    }
    if (debug) {
      println("  vm: load constant: ${constant.id} ${constant.tag.name} $value")
    }
    push(value)
  }

  fun localAt(id: UInt): Any {
    val localSet = locals.last()
    return localSet.load(id)
  }

  fun loadLocal(id: UInt) {
    val value = localAt(id)
    if (debug) {
      println("  vm: load local: $id   $value")
    }
    push(value)
  }

  fun storeLocal(id: UInt) {
    val localSet = locals.last()
    val value = popAnyValue()
    if (debug) {
      println("  vm: store local: $id = $value")
    }
    localSet.store(id, value)
  }

  fun setNextInst(value: UInt) {
    inst = value
    autoNextInst = false
    if (debug) {
      println("  vm: next instruction: $value")
    }
  }

  fun pushReturnAddress(value: UInt) {
    if (debug) {
      println("  vm: push return address: $value")
    }
    returnAddressStack.add(value)
  }

  fun pushCallStack(value: UInt) {
    if (debug) {
      println("  vm: push call stack: $value")
    }
    callStack.add(value)
  }

  fun popCallStack() {
    val call = callStack.removeLast()
    if (debug) {
      println("  vm: pop call stack: $call")
    }
  }

  fun armReturnAddressIfSet() {
    val returnAddress = returnAddressStack.removeLastOrNull()
    if (returnAddress != null) {
      if (debug) {
        println("  vm: arm return address: $returnAddress")
      }
      setNextInst(returnAddress)
    } else {
      exit()
    }
  }

  fun push(item: Any) {
    if (debug) {
      println("  vm: push stack: $item")
    }
    stack.add(item)
  }

  fun popAnyValue(): Any {
    val value = stack.removeLast()
    if (debug) {
      println("  vm: pop stack: $value")
    }
    return value
  }

  inline fun <reified T> pop(): T = popAnyValue() as T

  fun exit() {
    if (debug) {
      println("  vm: exit")
    }
    exitFlag = true
  }

  fun reset() {
    if (debug) {
      println("vm: reset")
    }
    stack.clear()
    callStack.clear()
    callStack.add(0u)
    locals.clear()
    locals.add(LocalSlots())
    inst = 0u
    exitFlag = false
    autoNextInst = true
  }

  fun frame(at: UInt = inst): StackFrame? {
    val (symbolInfo, rel) = world.symbolTable.lookup(at) ?: (null to null)
    return if (symbolInfo != null && rel != null) {
      StackFrame(symbolInfo, rel)
    } else null
  }
}
