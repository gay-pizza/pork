package gay.pizza.pork.bytecode

class MutableConstantPool {
  private val pool = mutableListOf<Constant>()

  fun assign(content: ByteArray): UInt {
    for (constant in pool) {
      if (constant.value.contentEquals(content)) {
        return constant.id
      }
    }
    val id = pool.size.toUInt()
    pool.add(Constant(id, content))
    return id
  }

  fun all(): List<Constant> = pool
}
