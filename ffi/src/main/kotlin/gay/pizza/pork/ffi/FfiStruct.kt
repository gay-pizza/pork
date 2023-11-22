package gay.pizza.pork.ffi

import com.kenai.jffi.InvocationBuffer
import com.kenai.jffi.MemoryIO
import com.kenai.jffi.Struct
import gay.pizza.pork.execution.None

class FfiStruct(val ffiTypeRegistry: FfiTypeRegistry) : FfiType {
  private val fields = LinkedHashMap<String, FfiStructField>()
  private var internalStructType: Struct? = null
  val ffiStruct: Struct
    get() {
      if (internalStructType == null) {
        internalStructType = Struct.newStruct(*ffiTypes.map {
          FfiNativeProvider.typeConversion(it)
        }.toTypedArray())
      }
      return internalStructType!!
    }
  private var internalTypes: List<FfiType>? = null
  val ffiTypes: List<FfiType>
    get() {
      if (internalTypes == null) {
        internalTypes = fields.values.map { ffiTypeRegistry.required(it.type) }
      }
      return internalTypes!!
    }

  data class FfiStructField(val name: String, val type: String)

  fun add(field: String, type: String) {
    fields[field] = FfiStructField(field, type)
    internalStructType = null
    internalTypes = null
  }

  override val size: Long
    get() = ffiStruct.size().toLong()

  override fun put(buffer: InvocationBuffer, value: Any?) {
    when (value) {
      is Map<*, *> -> {
        for (field in fields.values) {
          val item = value[field.name] ?: None
          val itemType = ffiTypeRegistry.required(field.type)
          itemType.put(buffer, item)
        }
      }

      is List<*> -> {
        for ((index, field) in fields.values.withIndex()) {
          val itemType = ffiTypeRegistry.required(field.type)
          itemType.put(buffer, value[index])
        }
      }

      is None -> {}

      else -> {
        throw RuntimeException("Unknown value type: $value")
      }
    }
  }

  override fun value(ffi: Any?): Any {
    return None
  }

  override fun read(address: FfiAddress, offset: Int): Any {
    val bytes = ByteArray(size.toInt()) { 0 }
    MemoryIO.getInstance().getByteArray(address.location, bytes, offset, size.toInt())
    return bytes
  }

  fun get(field: String, address: FfiAddress): Any {
    var indexWithoutAlignment = 0L
    var type: FfiType? = null
    for ((index, key) in fields.keys.withIndex()) {
      if (key == field) {
        type = ffiTypes[index]
        break
      }
      indexWithoutAlignment += ffiTypes[index].size
    }

    if (type == null) {
      throw RuntimeException("Unable to read unknown field $field from struct.")
    }

    return type.read(address, indexWithoutAlignment.toInt())
  }
}
