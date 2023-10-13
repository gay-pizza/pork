package gay.pizza.pork.ffi

class FfiTypeRegistry(val parent: FfiTypeRegistry? = null) {
  private val types = mutableMapOf<String, FfiType>()

  fun registerPrimitiveTypes() {
    for (type in FfiPrimitiveType.entries) {
      add(type.id, type)
    }
    add("size_t", FfiPrimitiveType.Long)
  }

  fun add(name: String, type: FfiType) {
    types[name] = type

    if (type is FfiStruct) {
      types["${name}*"] = FfiPrimitiveType.Pointer
    }
  }

  fun lookup(name: String): FfiType? = types[name] ?: parent?.lookup(name)
  fun required(name: String): FfiType = lookup(name) ?: throw RuntimeException("Unknown ffi type: $name")

  fun fork(): FfiTypeRegistry = FfiTypeRegistry(this)
}
