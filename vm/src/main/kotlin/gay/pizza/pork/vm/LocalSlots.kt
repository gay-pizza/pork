package gay.pizza.pork.vm

class LocalSlots {
  private var slots = arrayOfNulls<Any>(4)

  fun load(index: UInt): Any {
    return slots[index.toInt()]
      ?: throw VirtualMachineException("Attempted to access local $index that is not stored")
  }

  fun store(index: UInt, value: Any) {
    if (index >= slots.size.toUInt()) {
      resize(index + 4u)
    }
    slots[index.toInt()] = value
  }

  private fun resize(count: UInt) {
    val values = arrayOfNulls<Any>(count.toInt())
    for ((i, value) in slots.withIndex()) {
      values[i] = value
    }
    slots = values
  }
}
