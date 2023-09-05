package gay.pizza.pork.buildext.ast

class AstType(val name: String, var parent: AstType? = null) {
  private val internalValues = mutableListOf<AstValue>()
  private val internalEnums = mutableListOf<AstEnum>()

  val values: List<AstValue>
    get() = internalValues

  val enums: List<AstEnum>
    get() = internalEnums

  internal fun addValue(value: AstValue) {
    internalValues.add(value)
  }

  internal fun addEnum(enum: AstEnum) {
    internalEnums.add(enum)
  }

  fun isParentAbstract(value: AstValue): Boolean {
    if (parent == null) {
      return false
    }

    var current = parent
    while (current != null) {
      val abstract = current.values.firstOrNull {
        it.name == value.name && it.abstract
      }
      if (abstract != null) {
        return true
      }
      current = current.parent
    }
    return false
  }

  override fun toString(): String = "AstType(${name})"
}
