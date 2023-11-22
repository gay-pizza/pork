package gay.pizza.pork.ffi

import com.kenai.jffi.InvocationBuffer
import com.kenai.jffi.MemoryIO
import gay.pizza.pork.execution.None

enum class FfiPrimitiveType(
  val id: kotlin.String,
  override val size: kotlin.Long,
  val numberConvert: (Number.() -> Number)? = null,
  val nullableConversion: (Any?.() -> Any)? = null,
  val notNullConversion: (Any.() -> Any)? = null
) : FfiType {
  Byte("byte", 1, numberConvert = { toByte() }),
  UnsignedByte("unsigned byte", 1, numberConvert = { toByte()}),
  Short("short", 2, numberConvert = { toShort() }),
  UnsignedShort("unsigned short", 2, numberConvert = { toShort() }),
  Int("int", 4, numberConvert = { toInt() }),
  UnsignedInt("unsigned int", 4, numberConvert = { toInt() }),
  Float("float", 4, numberConvert = { toFloat() }),
  Long("long", 8, numberConvert = { toLong() }),
  UnsignedLong("unsigned long", 8, numberConvert = { toLong() }),
  Double("double", 8, numberConvert = { toDouble() }),
  String("char*", 8, nullableConversion = {
    if (this is FfiString) {
      this
    } else FfiString.allocate(toString())
  }),
  Pointer("void*", 8, nullableConversion = {
    when (this) {
      is FfiAddress -> this
      is FfiString -> this.address
      is None -> FfiAddress.Null
      is Number -> FfiAddress(this.toLong())
      else -> FfiAddress.Null
    }
  }),
  Void("void", 0);

  override fun put(buffer: InvocationBuffer, value: Any?) {
    if (numberConvert != null) {
      push(buffer, numberConvert(id, value, numberConvert))
    }

    if (notNullConversion != null) {
      push(buffer, notNullConvert(id, value, notNullConversion))
    }

    if (nullableConversion != null) {
      val result = nullableConvert(value, nullableConversion) ?: FfiAddress.Null
      push(buffer, result)
    }
  }

  private fun <T> notNullConvert(type: kotlin.String, value: Any?, into: Any.() -> T): T {
    if (value == null) {
      throw RuntimeException("Null values cannot be used for converting to type $type")
    }
    return into(value)
  }

  private fun <T> nullableConvert(value: Any?, into: Any.() -> T): T? {
    if (value == null || value == None) {
      return null
    }
    return into(value)
  }

  private fun <T> numberConvert(type: kotlin.String, value: Any?, into: Number.() -> T): T {
    if (value == null || value == None) {
      throw RuntimeException("Null values cannot be used for converting to numeric type $type")
    }

    if (value !is Number) {
      throw RuntimeException("Cannot convert value '$value' into type $type")
    }
    return into(value)
  }

  override fun value(ffi: Any?): Any {
    if (ffi == null) {
      return None
    }

    if (ffi is FfiString) {
      val content = ffi.read()
      ffi.free()
      return content
    }
    return ffi
  }

  override fun read(address: FfiAddress, offset: kotlin.Int): Any {
    val actual = address.location + offset
    return when (this) {
      UnsignedByte, Byte -> MemoryIO.getInstance().getByte(actual)
      UnsignedShort, Short -> MemoryIO.getInstance().getShort(actual)
      UnsignedInt, Int -> MemoryIO.getInstance().getInt(actual)
      UnsignedLong, Long -> MemoryIO.getInstance().getLong(actual)
      Float -> MemoryIO.getInstance().getFloat(actual)
      Double -> MemoryIO.getInstance().getDouble(actual)
      Pointer -> MemoryIO.getInstance().getAddress(actual)
      String -> FfiString(FfiAddress(actual))
      Void -> None
    }
  }

  companion object {
    fun push(buffer: InvocationBuffer, value: Any): Unit = when (value) {
      is kotlin.Byte -> buffer.putByte(value.toInt())
      is kotlin.Short -> buffer.putShort(value.toInt())
      is kotlin.Int -> buffer.putInt(value)
      is kotlin.Long -> buffer.putLong(value)
      is FfiAddress -> buffer.putAddress(value.location)
      is FfiString -> buffer.putAddress(value.address.location)
      else -> throw RuntimeException("Unknown buffer insertion: $value (${value.javaClass.name})")
    }
  }
}
