package gay.pizza.pork.buildext.ast

class AstType(val name: String, var parent: AstType? = null) {
  private var internalValues: MutableList<AstValue>? = null
  private val internalEnums = mutableListOf<AstEnum>()

  val values: List<AstValue>?
    get() = internalValues

  val enums: List<AstEnum>
    get() = internalEnums

  internal fun markHasValues() {
    if (internalValues == null) {
      internalValues = mutableListOf()
    }
  }

  internal fun addValue(value: AstValue) {
    markHasValues()
    internalValues!!.add(value)
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
      val abstract = current.values?.firstOrNull {
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
