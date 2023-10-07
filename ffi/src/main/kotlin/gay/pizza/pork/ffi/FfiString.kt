package gay.pizza.pork.ffi

import com.kenai.jffi.MemoryIO

class FfiString(val address: FfiAddress) {
  fun read(): String = address.readString()

  fun free() {
    address.free()
  }

  companion object {
    fun allocate(input: String): FfiString {
      val bytes = input.toByteArray()
      val buffer = FfiAddress.allocate(bytes.size + 1L)
      MemoryIO.getInstance().putZeroTerminatedByteArray(buffer.location, bytes, 0, bytes.size)
      return FfiString(buffer)
    }
  }
}
