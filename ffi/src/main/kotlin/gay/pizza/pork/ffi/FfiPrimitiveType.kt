package gay.pizza.pork.ffi

import gay.pizza.pork.evaluator.None

enum class FfiPrimitiveType(
  val id: kotlin.String,
  override val size: kotlin.Int,
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
  String("char*", 8, nullableConversion = { toString() }),
  Pointer("void*", 8, nullableConversion = {
    if (this is kotlin.Long) {
      com.sun.jna.Pointer(this)
    } else if (this == None) {
      com.sun.jna.Pointer.NULL
    } else this as com.sun.jna.Pointer
  })
}
