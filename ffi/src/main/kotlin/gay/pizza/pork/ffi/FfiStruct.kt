package gay.pizza.pork.ffi

class FfiStruct : FfiType {
  private val fields = mutableListOf<FfiStructField>()

  data class FfiStructField(val name: String, val type: FfiType)

  fun add(field: String, type: FfiType) {
    fields.add(FfiStructField(field, type))
  }

  override val size: Long
    get() = fields.sumOf { it.type.size }
}
