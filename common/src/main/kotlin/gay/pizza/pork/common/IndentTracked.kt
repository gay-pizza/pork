package gay.pizza.pork.common

abstract class IndentTracked(val indent: String) {
  private var internalIndentLevel = 0
  private var indentLevelText = ""

  val indentLevel: Int
    get() = internalIndentLevel

  fun emitIndent() {
    emit(indentLevelText)
  }

  fun emitIndented(text: String) {
    emitIndent()
    emit(text)
  }

  fun emitIndentedLine(line: String) {
    emitIndent()
    emitLine(line)
  }

  fun increaseIndent() {
    internalIndentLevel++
    indentLevelText += indent
  }

  fun decreaseIndent() {
    internalIndentLevel--
    indentLevelText = indent.repeat(indentLevel)
  }

  inline fun indented(block: IndentTracked.() -> Unit) {
    increaseIndent()
    block(this)
    decreaseIndent()
  }

  abstract fun emit(text: String)
  abstract fun emitLine(text: String)
}
