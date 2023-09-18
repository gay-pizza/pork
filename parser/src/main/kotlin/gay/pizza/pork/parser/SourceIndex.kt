package gay.pizza.pork.parser

data class SourceIndex(val index: Int, val line: Int, val column: Int, val locationReliable: Boolean = true) {
  companion object {
    fun zero(): SourceIndex = SourceIndex(0, 1, 0)
    fun indexOnly(index: Int) = SourceIndex(index, 0, 0, locationReliable = false)
  }

  override fun toString(): String = if (locationReliable) "${line}:${column}" else "$index"
}
