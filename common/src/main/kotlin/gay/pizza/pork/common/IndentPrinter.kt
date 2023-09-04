package gay.pizza.pork.common

class IndentPrinter(
  val buffer: StringBuilder = StringBuilder(),
  val indent: String = "  "
) : Appendable by buffer, CharSequence by buffer {
  private var indentLevel: Int = 0
  private var indentLevelText: String = ""

  fun emitIndent() {
    append(indentLevelText)
  }

  fun increaseIndent() {
    indentLevel++
    indentLevelText = indent.repeat(indentLevel)
  }

  fun decreaseIndent() {
    indentLevel--
    indentLevelText = indent.repeat(indentLevel)
  }

  override fun toString(): String = buffer.toString()
}
