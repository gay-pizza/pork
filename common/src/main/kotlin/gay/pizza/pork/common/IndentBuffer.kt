package gay.pizza.pork.common

import java.util.stream.IntStream

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

  override fun chars(): IntStream {
    return buffer.chars()
  }

  override fun codePoints(): IntStream {
    return buffer.codePoints()
  }
}
