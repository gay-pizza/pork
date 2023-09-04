package gay.pizza.pork.gradle.ast

class AstType(val name: String, var parent: AstType? = null) {
  private val internalValues = mutableListOf<AstValue>()

  val values: List<AstValue>
    get() = internalValues

  internal fun addValue(value: AstValue) {
    internalValues.add(value)
  }
}
