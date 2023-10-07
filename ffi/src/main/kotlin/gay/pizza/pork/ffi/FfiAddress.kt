package gay.pizza.pork.ffi

import com.kenai.jffi.MemoryIO

data class FfiAddress(val location: Long) {
  companion object {
    val Null = FfiAddress(0L)

    fun allocate(size: Long): FfiAddress =
      FfiAddress(MemoryIO.getInstance().allocateMemory(size, true))
  }

  fun read(size: Int): ByteArray = read(0, size)

  fun read(offset: Int, size: Int): ByteArray {
    val bytes = ByteArray(size) { 0 }
    MemoryIO.getInstance().getByteArray(location, bytes, offset, size)
    return bytes
  }

  fun readNullTerminated(): ByteArray =
    MemoryIO.getInstance().getZeroTerminatedByteArray(location)

  fun readString(): String = String(readNullTerminated())

  fun free(): Unit = MemoryIO.getInstance().freeMemory(location)
}
