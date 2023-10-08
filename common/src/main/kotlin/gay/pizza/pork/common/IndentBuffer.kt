package gay.pizza.pork.common

class IndentBuffer(
  val buffer: StringBuilder = StringBuilder(),
  indent: String = "  "
) : IndentTracked(indent), Appendable by buffer, CharSequence by buffer {
  override fun emit(text: String) {
    append(text)
  }

  override fun emitLine(text: String) {
    appendLine(text)
  }

  override fun toString(): String = buffer.toString()
}
