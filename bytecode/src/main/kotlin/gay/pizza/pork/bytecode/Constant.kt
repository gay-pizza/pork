package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class Constant(val id: UInt, val tag: ConstantTag, val value: ByteArray) {
  fun readAsString(): String {
    if (tag != ConstantTag.String) {
      throw RuntimeException("Constant $id is not tagged as a string")
    }
    return String(value)
  }

  fun readAsNativeDefinition(): List<String> {
    val defs = mutableListOf<String>()
    val buffer = mutableListOf<Byte>()
    for (b in value) {
      if (b == 0.toByte()) {
        defs.add(String(buffer.toByteArray()))
        buffer.clear()
        continue
      }
      buffer.add(b)
    }

    if (buffer.isNotEmpty()) {
      defs.add(String(buffer.toByteArray()))
    }

    return defs
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    other as Constant

    if (id != other.id) return false
    if (!value.contentEquals(other.value)) return false
    return true
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + value.contentHashCode()
    return result
  }
}
