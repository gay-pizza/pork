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

  fun emitIndentedLine(line: String) {
    emitIndent()
    appendLine(line)
  }

  fun increaseIndent() {
    indentLevel++
    indentLevelText += indent
  }

  fun decreaseIndent() {
    indentLevel--
    indentLevelText = indent.repeat(indentLevel)
  }

  inline fun indented(block: IndentPrinter.() -> Unit) {
    increaseIndent()
    block(this)
    decreaseIndent()
  }

  override fun toString(): String = buffer.toString()
}
