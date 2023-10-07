package gay.pizza.pork.ffi

import com.kenai.jffi.MemoryIO

class FfiStringWrapper(input: String) {
  val address: Long

  init {
    val bytes = input.toByteArray()
    address = MemoryIO.getInstance().allocateMemory((bytes.size + 1).toLong(), true)
    MemoryIO.getInstance().putZeroTerminatedByteArray(address, bytes, 0, bytes.size)
  }

  fun free() {
    MemoryIO.getInstance().freeMemory(address)
  }
}
