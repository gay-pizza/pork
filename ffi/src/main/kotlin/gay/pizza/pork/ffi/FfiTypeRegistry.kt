package gay.pizza.pork.ffi

class FfiTypeRegistry {
  private val types = mutableMapOf<String, FfiType>()

  init {
    for (type in FfiPrimitiveType.entries) {
      add(type.id, type)
    }
    add("size_t", FfiPrimitiveType.Long)
  }

  fun add(name: String, type: FfiType) {
    types[name] = type
  }

  fun lookup(name: String): FfiType? = types[name]
}
