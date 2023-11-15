package gay.pizza.pork.common

object ByteRepresentation {
  fun encode(value: Int): ByteArray {
    val buffer = ByteArray(4)
    encode(value, buffer, 0)
    return buffer
  }

  fun encode(value: Long): ByteArray {
    val buffer = ByteArray(8)
    encode(value, buffer, 0)
    return buffer
  }

  fun encode(value: Double): ByteArray =
    encode(value.toRawBits())

  fun encode(value: Int, buffer: ByteArray, offset: Int) {
    buffer[offset + 0] = (value shr 0).toByte()
    buffer[offset + 1] = (value shr 8).toByte()
    buffer[offset + 2] = (value shr 16).toByte()
    buffer[offset + 3] = (value shr 24).toByte()
  }

  fun encode(value: Long, buffer: ByteArray, offset: Int) {
    buffer[offset + 0] = (value shr 0).toByte()
    buffer[offset + 1] = (value shr 8).toByte()
    buffer[offset + 2] = (value shr 16).toByte()
    buffer[offset + 3] = (value shr 24).toByte()
    buffer[offset + 4] = (value shr 32).toByte()
    buffer[offset + 5] = (value shr 40).toByte()
    buffer[offset + 6] = (value shr 48).toByte()
    buffer[offset + 7] = (value shr 56).toByte()
  }

  fun encode(value: Double, buffer: ByteArray, offset: Int) {
    encode(value.toRawBits(), buffer, offset)
  }
}
