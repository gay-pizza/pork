package gay.pizza.pork.ffi

import com.kenai.jffi.InvocationBuffer
import gay.pizza.pork.evaluator.None
import java.util.*

class FfiStruct : FfiType {
  private val fields = TreeMap<String, FfiStructField>()

  data class FfiStructField(val name: String, val type: FfiType)

  fun add(field: String, type: FfiType) {
    fields[field] = FfiStructField(field, type)
  }

  override val size: Long
    get() = fields.values.sumOf { it.type.size }

  override fun put(buffer: InvocationBuffer, value: Any?) {
    when (value) {
      is Map<*, *> -> {
        for (field in fields.values) {
          val item = value[field.name] ?: None
          field.type.put(buffer, item)
        }
      }

      is List<*> -> {
        for ((index, field) in fields.values.withIndex()) {
          field.type.put(buffer, value[index])
        }
      }

      else -> {
        throw RuntimeException("Unknown value type: $value")
      }
    }
  }

  override fun value(ffi: Any?): Any {
    return None
  }
}
