package gay.pizza.pork.bytecode

class MutableConstantPool {
  private val pool = mutableListOf<Constant>()

  fun assign(tag: ConstantTag, content: ByteArray): UInt {
    for (constant in pool) {
      if (constant.value.contentEquals(content) && tag == constant.tag) {
        return constant.id
      }
    }
    val id = pool.size.toUInt()
    pool.add(Constant(id = id, tag = tag, value = content))
    return id
  }

  fun build(): ConstantPool = ConstantPool(pool)
}
